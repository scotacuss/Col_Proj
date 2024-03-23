package com.example.accel

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


class Settings : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val cur_grav = findViewById<TextView>(R.id.current_grav)
        val new_grav = findViewById<EditText>(R.id.new_grav)

        val sync = findViewById<Button>(R.id.sync)

        cur_grav.text = " $grav_strength m/s^2 "



        new_grav.setOnEditorActionListener { _, _, _ ->
            // Get the text from the EditText
            val inputText = new_grav.text.toString()

            // Try to parse the text to Float
            val inputValue = inputText.toDoubleOrNull()

            // If parsing successful, update grav_strength
            if (inputValue != null) {
                grav_strength = inputValue
                cur_grav.text = " $grav_strength m/s^2 "
            }
            // Consume the event
            true
        }


    }
}