package com.example.accel

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView

var current_preset = "moon"


var medium_density: Double = 1.293
var medium_gravity: Double = 9.81

var ball_mass: Double = 1.0
var COR: Double = 1.0
var Diameter: Int = 100

class Settings : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val new_grav = findViewById<EditText>(R.id.new_grav)
        val new_den = findViewById<EditText>(R.id.new_density)
        val cur_pre = findViewById<TextView>(R.id.cur_preset)
        val earth_but = findViewById<Button>(R.id.earth)
        val moon_but = findViewById<Button>(R.id.moon)
        val mars_but = findViewById<Button>(R.id.mars)


        cur_pre.text = current_preset
        new_den.hint = medium_density.toString()
        new_grav.hint = medium_gravity.toString()

        earth_but.setOnClickListener {
            current_preset = "earth"
            cur_pre.text = current_preset
        }
        moon_but.setOnClickListener {
            current_preset = "moon"
            cur_pre.text = current_preset
        }
        mars_but.setOnClickListener {
            current_preset = "mars"
            cur_pre.text = current_preset
        }











        new_grav.setOnEditorActionListener { _, _, _ ->
            val inputText = new_grav.text.toString()
            val inputValue = inputText.toDoubleOrNull()
            if (inputValue != null) {
                medium_gravity = inputValue
            }
            true
        }




        new_den.setOnEditorActionListener { _, _, _ ->
            val inputText = new_den.text.toString()
            val inputValue = inputText.toDoubleOrNull()
            if (inputValue != null) {
                medium_density = inputValue
            }
            true
        }



    }
}