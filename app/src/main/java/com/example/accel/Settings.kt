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

var current_grav = "earth"

class Settings : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val cur_grav = findViewById<TextView>(R.id.current_grav)
        val new_grav = findViewById<EditText>(R.id.new_grav)

        val cur_den = findViewById<TextView>(R.id.current_density)
        val new_den = findViewById<EditText>(R.id.new_density)

        val dpdw = findViewById<Spinner>(R.id.spinner1)

        // Define the array of options
        val options = arrayOf("earth", "moon", "mars")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dpdw.adapter = adapter
        cur_den.text = current_grav



        dpdw.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Item selected, perform your actions here
                val selectedItem = dpdw.getItemAtPosition(position).toString()
                // Do something with the selected item
                current_grav = selectedItem
                cur_den.text = current_grav
//
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }

        }




//        cur_den.text = current_grav
        cur_grav.text = " $grav_strength m/s^2"



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