package com.example.csci3081_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val iconRadioGroup = findViewById<RadioGroup>(R.id.iconRadioGroup)
        val difficultyRadioGroup = findViewById<RadioGroup>(R.id.difficultyRadioGroup)
        val beginButton = findViewById<Button>(R.id.beginButton)
        val resetScoreButton = findViewById<Button>(R.id.resetScoreButton)

        beginButton.setOnClickListener {
            val selectedIconId = iconRadioGroup.checkedRadioButtonId
            val selectedIcon = findViewById<RadioButton>(selectedIconId).text.toString()

            val selectedDifficultyId = difficultyRadioGroup.checkedRadioButtonId
            val difficulty = when (selectedDifficultyId) {
                R.id.radioEasy -> "Easy"
                R.id.radioMedium -> "Medium"
                R.id.radioHard -> "Hard"
                else -> "Easy"
            }

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("PLAYER_ICON", selectedIcon)
                putExtra("DIFFICULTY", difficulty)
            }
            startActivity(intent)
        }

        resetScoreButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("TicTacToeScores", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                clear()
                apply()
            }
            Toast.makeText(this, "Scores have been reset!", Toast.LENGTH_SHORT).show()
        }
    }
}
