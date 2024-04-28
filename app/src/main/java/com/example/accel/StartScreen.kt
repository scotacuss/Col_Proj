package com.example.accel

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StartScreen : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_screen)

        val strtBut = findViewById<Button>(R.id.strt_but)
        strtBut.setOnClickListener {
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val set_but = findViewById<Button>(R.id.setting_but)
        set_but.setOnClickListener {
            val intent: Intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }




    }
}