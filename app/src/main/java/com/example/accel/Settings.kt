package com.example.accel

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlin.math.pow
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView

var current_preset = "moon"


var medium_density: Double = 5.96*(10.0.pow(-27))
var medium_gravity: Double = 1.62

var ball_preset: String = "rubber"
var ball_density: Double = 1522.0
var COR: Double = 0.9
var diameter: Int = 100
var ball_colour: String = "black"

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
        val main_but = findViewById<Button>(R.id.main_but)

        main_but.setOnClickListener {
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        cur_pre.text = current_preset
        new_den.hint = medium_density.toString()
        new_grav.hint = medium_gravity.toString()

        earth_but.setOnClickListener {
            current_preset = "earth"
            medium_density = 1.2
            medium_gravity = 9.81
            cur_pre.text = current_preset
            new_den.hint = medium_density.toString()
            new_grav.hint = medium_gravity.toString()
        }
        moon_but.setOnClickListener {
            current_preset = "moon"
            medium_density = 5.96*(10.0.pow(-27))
            medium_gravity = 1.62
            new_den.hint = medium_density.toString()
            new_grav.hint = medium_gravity.toString()
            cur_pre.text = current_preset
        }
        mars_but.setOnClickListener {
            current_preset = "mars"
            medium_density = 0.02
            medium_gravity = 3.71
            cur_pre.text = current_preset
            new_den.hint = medium_density.toString()
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

        val cur_ball = findViewById<TextView>(R.id.cur_ball)
        val limestone_but = findViewById<Button>(R.id.limestone)
        val rubber_but = findViewById<Button>(R.id.rubber)
        val gold_but = findViewById<Button>(R.id.gold)
        val pro_den = findViewById<TextView>(R.id.new_proden)
        val cor = findViewById<TextView>(R.id.new_damp)
        val diam = findViewById<TextView>(R.id.new_diam)

        cur_ball.text = ball_preset
        pro_den.hint = ball_density.toString()
        cor.hint = COR.toString()
        diam.hint = diameter.toString()

        limestone_but.setOnClickListener {
            ball_preset = "limestone"
            cur_ball.text = ball_preset
            ball_density = 2711.0
            COR = 0.5
            pro_den.hint = ball_density.toString()
            cor.hint = COR.toString()
            ball_colour = "grey"
        }

        rubber_but.setOnClickListener {
            ball_preset = "rubber"
            cur_ball.text = ball_preset
            ball_density = 1522.0
            COR = 0.9
            pro_den.hint = ball_density.toString()
            cor.hint = COR.toString()
            ball_colour = "black"
        }

        gold_but.setOnClickListener {
            ball_preset = "gold"
            cur_ball.text = ball_preset
            ball_density = 19320.0
            COR = 0.3
            pro_den.hint = ball_density.toString()
            cor.hint = COR.toString()
            ball_colour = "yellow"
        }

        pro_den.setOnEditorActionListener { _, _, _ ->
            val inputText = pro_den.text.toString()
            val inputValue = inputText.toDoubleOrNull()
            if (inputValue != null) {
                ball_density = inputValue
            }
            true
        }

        cor.setOnEditorActionListener { _, _, _ ->
            val inputText = cor.text.toString()
            val inputValue = inputText.toDoubleOrNull()
            if (inputValue != null) {
                COR = inputValue
            }
            true
        }

        diam.setOnEditorActionListener { _, _, _ ->
            val inputText = diam.text.toString()
            val inputValue = inputText.toIntOrNull()
            if (inputValue != null) {
                diameter = inputValue
            }
            true
        }







    }
}