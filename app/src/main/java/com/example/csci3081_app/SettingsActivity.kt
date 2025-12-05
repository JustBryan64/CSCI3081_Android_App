package com.example.csci3081_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton // Use this one
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val iconRadioGroup = findViewById<RadioGroup>(R.id.iconRadioGroup)
        val difficultyRadioGroup = findViewById<RadioGroup>(R.id.difficultyRadioGroup)
        val beginButton = findViewById<Button>(R.id.beginButton)

        beginButton.setOnClickListener {
            // Get selected player icon
            val selectedIconId = iconRadioGroup.checkedRadioButtonId
            // Use the standard android.widget.RadioButton
            val selectedIcon = findViewById<RadioButton>(selectedIconId).text.toString()

            // Get selected difficulty
            val selectedDifficultyId = difficultyRadioGroup.checkedRadioButtonId
            val difficulty = when (selectedDifficultyId) {
                R.id.radioEasy -> "Easy"
                R.id.radioMedium -> "Medium"
                R.id.radioHard -> "Hard"
                else -> "Easy"
            }

            // Start MainActivity and pass the settings
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("PLAYER_ICON", selectedIcon)
                putExtra("DIFFICULTY", difficulty)
            }
            startActivity(intent)
        }
    }
}
