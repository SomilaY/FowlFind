package com.FowlFind.googlemaps

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class Howto : AppCompatActivity()  {

    private lateinit var hotspotImageView: ImageView
    private lateinit var listImageView: ImageView
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.howtouse)

        hotspotImageView = findViewById(R.id.Hotspot)
        listImageView = findViewById(R.id.List)
        profileImageView = findViewById(R.id.Profile)

        // Set click listeners for the ImageView buttons
        hotspotImageView.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        listImageView.setOnClickListener {
            val intent = Intent(this, PastObservationsActivity::class.java)
            startActivity(intent)
        }

        profileImageView.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }


    }
}