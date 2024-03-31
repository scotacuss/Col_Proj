package com.example.accel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

var shape = "circle"
var len = 200
var wid = 200
var damp = 1.0
var obstacle_created = 0

var created_obs: MutableList<Array<Any>> = mutableListOf(arrayOf(250, 250, 0.8, "circle"))
class ObstacleCreator : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_obstacle_creator)



        var obs_shape = findViewById<EditText>(R.id.obs_shape)
        var obs_len = findViewById<EditText>(R.id.obs_len)
        var obs_width = findViewById<EditText>(R.id.obs_width)
        var obs_damp = findViewById<EditText>(R.id.obs_damp)
        var obs_creater = findViewById<Button>(R.id.creator)

        obs_creater.setOnClickListener {
            created_obs.add(arrayOf(len,wid,damp,shape))
        }

        obs_shape.hint = shape
        obs_len.hint = len.toString()
        obs_width.hint = wid.toString()
        obs_damp.hint = damp.toString()



        val intent = Intent(this, MainActivity::class.java)
        obs_shape.setOnEditorActionListener { _, _, _ ->
            val inputText = obs_shape.text.toString()
            val inputValue = inputText.toString()
            if (inputValue != null) {
                shape = inputValue
            }
            true
        }
        obs_len.setOnEditorActionListener { _, _, _ ->
            val inputText = obs_len.text.toString()
            val inputValue = inputText.toIntOrNull()
            if (inputValue != null) {
                len = inputValue
            }
            true
        }
        obs_width.setOnEditorActionListener { _, _, _ ->
            val inputText = obs_width.text.toString()
            val inputValue = inputText.toIntOrNull()
            if (inputValue != null) {
                wid = inputValue
            }
            true
        }
        obs_damp.setOnEditorActionListener { _, _, _ ->
            val inputText = obs_damp.text.toString()
            val inputValue = inputText.toDoubleOrNull()
            if (inputValue != null) {
                damp = inputValue
            }
            true
        }



    }
}