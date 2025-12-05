package com.example.csci3081_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var playAgainButton: Button
    private lateinit var backToSettingsButton: Button
    private lateinit var boardGridLayout: GridLayout

    private lateinit var gameLogic: GameLogic
    private val boardButtons = Array(3) { arrayOfNulls<Button>(3) }
    private var currentPlayer = "X"
    private var isGameActive = true

    private lateinit var playerIcon: String
    private lateinit var aiIcon: String
    private lateinit var difficulty: String

    private lateinit var sharedPreferences: SharedPreferences
    private var playerScore = 0
    private var aiScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameLogic = GameLogic()

        sharedPreferences = getSharedPreferences("TicTacToeScores", Context.MODE_PRIVATE)
        loadScores()

        playerIcon = intent.getStringExtra("PLAYER_ICON") ?: "X"
        aiIcon = if (playerIcon == "X") "O" else "X"
        difficulty = intent.getStringExtra("DIFFICULTY") ?: "Easy"

        statusTextView = findViewById(R.id.statusTextView)
        scoreTextView = findViewById(R.id.scoreTextView)
        playAgainButton = findViewById(R.id.playAgainButton)
        backToSettingsButton = findViewById(R.id.backToSettingsButton)
        boardGridLayout = findViewById(R.id.boardGridLayout)

        setupBoard()
        updateScoreDisplay()

        playAgainButton.setOnClickListener { resetGame() }
        backToSettingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        startGame()
    }

    private fun startGame() {
        currentPlayer = "X"
        if (currentPlayer == aiIcon) {
            statusTextView.text = "AI's Turn"
            isGameActive = false
            Handler(Looper.getMainLooper()).postDelayed({ aiMove() }, 500)
        } else {
            statusTextView.text = "Your Turn"
            isGameActive = true
        }
    }

    private fun setupBoard() {
        val scale = resources.displayMetrics.density
        val cellSizeInPx = (100 * scale).toInt()
        val marginInPx = (2 * scale).toInt()

        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val cell = Button(this)
                val params = GridLayout.LayoutParams()
                params.width = cellSizeInPx
                params.height = cellSizeInPx
                params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx)
                cell.layoutParams = params
                cell.textSize = 40f
                cell.setBackgroundColor(Color.WHITE)
                cell.setOnClickListener { onCellClicked(i, j) }
                boardGridLayout.addView(cell)
                boardButtons[i][j] = cell
            }
        }
    }

    private fun onCellClicked(row: Int, col: Int) {
        if (!isGameActive || currentPlayer == aiIcon || !gameLogic.makeMove(row, col, playerIcon)) {
            return
        }

        updateButton(row, col, playerIcon)

        if (gameLogic.checkForWinner(playerIcon)) {
            endGame(false)
        } else if (gameLogic.isBoardFull()) {
            endGame(true)
        } else {
            currentPlayer = aiIcon
            statusTextView.text = "AI's Turn"
            isGameActive = false
            Handler(Looper.getMainLooper()).postDelayed({ aiMove() }, 500)
        }
    }

    private fun aiMove() {
        val move = gameLogic.getAiMove(difficulty, aiIcon, playerIcon)
        move?.let { (row, col) ->
            gameLogic.makeMove(row, col, aiIcon)
            updateButton(row, col, aiIcon)
            if (gameLogic.checkForWinner(aiIcon)) {
                endGame(false)
            } else if (gameLogic.isBoardFull()) {
                endGame(true)
            } else {
                currentPlayer = playerIcon
                updateStatus()
                isGameActive = true
            }
        }
    }

    private fun updateButton(row: Int, col: Int, player: String) {
        val button = boardButtons[row][col]
        button?.text = player
        val color = if (player == "X") R.color.player_x_color else R.color.player_o_color
        button?.setTextColor(ContextCompat.getColor(this, color))
    }

    private fun updateStatus() {
        statusTextView.text = if (currentPlayer == playerIcon) "Your Turn" else "AI's Turn"
    }

    private fun endGame(isDraw: Boolean) {
        isGameActive = false
        when {
            isDraw -> {
                statusTextView.text = "It's a Draw!"
            }
            currentPlayer == playerIcon -> {
                statusTextView.text = "You Win!"
                playerScore++
            }
            else -> {
                statusTextView.text = "AI Wins!"
                aiScore++
            }
        }
        saveScores()
        updateScoreDisplay()
        playAgainButton.visibility = View.VISIBLE
        backToSettingsButton.visibility = View.VISIBLE
    }

    private fun resetGame() {
        gameLogic.resetBoard()
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                boardButtons[i][j]?.text = ""
            }
        }
        playAgainButton.visibility = View.GONE
        backToSettingsButton.visibility = View.GONE
        startGame()
    }

    private fun loadScores() {
        playerScore = sharedPreferences.getInt("PLAYER_SCORE", 0)
        aiScore = sharedPreferences.getInt("AI_SCORE", 0)
    }

    private fun saveScores() {
        with(sharedPreferences.edit()) {
            putInt("PLAYER_SCORE", playerScore)
            putInt("AI_SCORE", aiScore)
            apply()
        }
    }

    private fun updateScoreDisplay() {
        scoreTextView.text = "You: $playerScore - AI: $aiScore"
    }
}
