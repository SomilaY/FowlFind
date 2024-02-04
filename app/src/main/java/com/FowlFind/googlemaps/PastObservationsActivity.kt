package com.FowlFind.googlemaps

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class PastObservationsActivity : AppCompatActivity() {

    private lateinit var viewobservations: Button
    private lateinit var hotspotImageView: ImageView
    private lateinit var listImageView: ImageView
    private lateinit var profileImageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.past_observations)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

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

        // Initialize UI elements
        val imageView = findViewById<ImageView>(R.id.detailImageView)
        val speciesTextView = findViewById<TextView>(R.id.detailSpeciesText)
        val dateTextView = findViewById<TextView>(R.id.detailDateText)
        val notesTextView = findViewById<TextView>(R.id.detailNotesText)
        viewobservations = findViewById(R.id.buttonViewObservations)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PastObservationsActivity)
            adapter = ObservationsAdapter(emptyList()) // Set an empty adapter initially
        }

        db.collection("observations")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val observations = result.documents.mapNotNull { it.toObject(Observations::class.java) }
                viewManager = LinearLayoutManager(this)
                viewAdapter = ObservationsAdapter(observations)

                recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
                    setHasFixedSize(true)
                    layoutManager = viewManager
                    adapter = viewAdapter
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        viewobservations.setOnClickListener {
            val intent = Intent(this, ListObservations::class.java)
            startActivity(intent)
        }
    }
}