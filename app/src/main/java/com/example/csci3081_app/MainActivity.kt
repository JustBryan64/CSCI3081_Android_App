package com.example.csci3081_app

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.gridlayout.widget.GridLayout
import com.example.csci3081_app.R
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var statusTextView: TextView
    private lateinit var playAgainButton: Button
    private lateinit var boardGridLayout: GridLayout

    // Game state variables
    private val board = Array(3) { Array(3) { "" } }
    private val boardButtons = Array(3) { arrayOfNulls<Button>(3) }
    private var currentPlayer = "X"
    private var isGameActive = true

    // Settings from SettingsActivity
    private lateinit var playerIcon: String
    private lateinit var aiIcon: String
    private lateinit var difficulty: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Receive settings from Intent
        playerIcon = intent.getStringExtra("PLAYER_ICON") ?: "X"
        aiIcon = if (playerIcon == "X") "O" else "X"
        difficulty = intent.getStringExtra("DIFFICULTY") ?: "Easy"

        statusTextView = findViewById(R.id.statusTextView)
        playAgainButton = findViewById(R.id.playAgainButton)
        boardGridLayout = findViewById(R.id.boardGridLayout)

        setupBoard()

        playAgainButton.setOnClickListener {
            resetGame()
        }

        // If AI is 'X', it makes the first move
        if (currentPlayer == aiIcon) {
            statusTextView.text = "AI's Turn"
            // Delay AI move for better UX
            Handler(Looper.getMainLooper()).postDelayed({ aiMove() }, 500)
        } else {
            updateStatus()
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
        // Only allow move if it's the player's turn and the cell is valid
        if (board[row][col].isNotEmpty() || !isGameActive || currentPlayer == aiIcon) {
            return
        }

        makeMove(row, col, playerIcon)

        // Check game state after player's move
        if (checkForWinner()) {
            endGame(false)
        } else if (isBoardFull()) {
            endGame(true)
        } else {
            // It's now the AI's turn
            currentPlayer = aiIcon
            statusTextView.text = "AI's Turn"
            isGameActive = false // Disable clicks while AI is "thinking"
            Handler(Looper.getMainLooper()).postDelayed({ aiMove() }, 500)
        }
    }

    private fun aiMove() {
        if (!isGameActive && currentPlayer != aiIcon) { // Re-enable clicks after AI move
            isGameActive = true
        }
        var move: Pair<Int, Int>? = null
        when (difficulty) {
            "Easy" -> move = findRandomMove()
            "Medium" -> {
                // 50% chance to block/win, 50% chance for random move
                move = if (Random.nextBoolean()) {
                    findWinningMove(aiIcon) ?: findWinningMove(playerIcon) ?: findRandomMove()
                } else {
                    findRandomMove()
                }
            }
            "Hard" -> {
                // Best move: Win -> Block -> Center -> Corner -> Side
                move = findWinningMove(aiIcon) // 1. Try to win
                    ?: findWinningMove(playerIcon) // 2. Block opponent from winning
                            ?: findBestStrategicMove() // 3. Take a strategic spot
            }
        }

        move?.let { (row, col) ->
            makeMove(row, col, aiIcon)
            if (checkForWinner()) {
                endGame(false)
            } else if (isBoardFull()) {
                endGame(true)
            } else {
                currentPlayer = playerIcon
                updateStatus()
                isGameActive = true // Re-enable player clicks
            }
        }
    }

    private fun makeMove(row: Int, col: Int, player: String) {
        board[row][col] = player
        val button = boardButtons[row][col]
        button?.text = player
        val color = if (player == "X") R.color.player_x_color else R.color.player_o_color
        button?.setTextColor(ContextCompat.getColor(this, color))
    }

    // --- AI Strategy Functions ---

    private fun findWinningMove(player: String): Pair<Int, Int>? {
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].isEmpty()) {
                    board[i][j] = player // Try the move
                    if (checkForWinner(player)) {
                        board[i][j] = "" // Reset move
                        return Pair(i, j)
                    }
                    board[i][j] = "" // Reset move
                }
            }
        }
        return null
    }

    private fun findRandomMove(): Pair<Int, Int> {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].isEmpty()) {
                    emptyCells.add(Pair(i, j))
                }
            }
        }
        return emptyCells.random()
    }

    private fun findBestStrategicMove(): Pair<Int, Int> {
        // 1. Take the center if available
        if (board[1][1].isEmpty()) return Pair(1, 1)

        // 2. Take a corner
        val corners = listOf(Pair(0, 0), Pair(0, 2), Pair(2, 0), Pair(2, 2))
        val availableCorners = corners.filter { board[it.first][it.second].isEmpty() }
        if (availableCorners.isNotEmpty()) return availableCorners.random()

        // 3. Take a side
        return findRandomMove() // Fallback to any available spot
    }

    // --- Game State Functions ---

    private fun checkForWinner(player: String? = null): Boolean {
        val p = player ?: currentPlayer
        for (i in 0..2) {
            if (board[i][0] == p && board[i][1] == p && board[i][2] == p) return true
            if (board[0][i] == p && board[1][i] == p && board[2][i] == p) return true
        }
        if (board[0][0] == p && board[1][1] == p && board[2][2] == p) return true
        if (board[0][2] == p && board[1][1] == p && board[2][0] == p) return true
        return false
    }

    private fun isBoardFull(): Boolean {
        return board.all { row -> row.all { it.isNotEmpty() } }
    }

    private fun updateStatus() {
        statusTextView.text = if (currentPlayer == playerIcon) "Your Turn" else "AI's Turn"
    }

    private fun endGame(isDraw: Boolean) {
        isGameActive = false
        statusTextView.text = when {
            isDraw -> "It's a Draw!"
            currentPlayer == playerIcon -> "You Win!"
            else -> "AI Wins!"
        }
        playAgainButton.visibility = View.VISIBLE
    }

    private fun resetGame() {
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                board[i][j] = ""
                boardButtons[i][j]?.text = ""
            }
        }
        isGameActive = true
        currentPlayer = "X" // X always starts

        if (currentPlayer == aiIcon) {
            statusTextView.text = "AI's Turn"
            isGameActive = false
            Handler(Looper.getMainLooper()).postDelayed({ aiMove() }, 500)
        } else {
            updateStatus()
        }

        playAgainButton.visibility = View.GONE
    }
}
