package com.FowlFind.googlemaps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SettingsActivity : AppCompatActivity() {
    private lateinit var editTextMaxDistance: EditText
    private lateinit var radioGroupUnitSystem: RadioGroup
    private lateinit var infobutton: FloatingActionButton
    private lateinit var logoutbutton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        infobutton = findViewById(R.id.fabInfo)
        logoutbutton = findViewById(R.id.buttonLogout)

        infobutton.setOnClickListener {
            val intent = Intent(this, info::class.java)
            startActivity(intent)
        }

        logoutbutton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        editTextMaxDistance = findViewById(R.id.editTextMaxDistance)
        radioGroupUnitSystem = findViewById(R.id.radioGroupUnitSystem)

        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val maxDistance = prefs.getFloat("max_distance", 10f)
        val unitSystem = prefs.getString("unit_system", "metric")

        editTextMaxDistance.setText(maxDistance.toString())
        when (unitSystem) {
            "metric" -> radioGroupUnitSystem.check(R.id.radioButtonMetric)
            "imperial" -> radioGroupUnitSystem.check(R.id.radioButtonImperial)
        }

        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            val maxDistanceValue = editTextMaxDistance.text.toString().toFloat()
            val unitSystemValue = when (radioGroupUnitSystem.checkedRadioButtonId) {
                R.id.radioButtonMetric -> "metric"
                R.id.radioButtonImperial -> "imperial"
                else -> "metric" // Default to metric if none selected
            }

            val editor = prefs.edit()
            editor.putFloat("max_distance", maxDistanceValue)
            editor.putString("unit_system", unitSystemValue)
            editor.apply()

            finish()
        }
    }
}
