package com.FowlFind.googlemaps

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream


class ListObservations : AppCompatActivity() {

    private var capturedImage: Bitmap? = null
    private val observations: MutableList<Observation> = mutableListOf()

    private lateinit var buttonCaptureImage: Button
    private lateinit var buttonSaveObservation: Button
    private lateinit var editTextSpecies: EditText
    private lateinit var editTextObservationDate: EditText
    private lateinit var editTextNotes: EditText
    private lateinit var imageViewCapturedImage: ImageView
    private lateinit var hotspotImageView: ImageView
     private lateinit var listImageView: ImageView
     private lateinit var profileImageView: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listobservations)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference


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

        // Initialize buttons
        buttonCaptureImage = findViewById(R.id.buttonCaptureImage)
        buttonSaveObservation = findViewById(R.id.buttonSaveObservation)

        // Initialize EditText fields
        editTextSpecies = findViewById(R.id.editTextSpecies)
        editTextObservationDate = findViewById(R.id.editTextObservationDate)
        editTextNotes = findViewById(R.id.editTextNotes)

        // Initialize ImageView
        imageViewCapturedImage = findViewById(R.id.imageViewCapturedImage)


        buttonCaptureImage.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
        }

        buttonSaveObservation.setOnClickListener {
            val species = editTextSpecies.text.toString()
            val observationDate = editTextObservationDate.text.toString()
            val notes = editTextNotes.text.toString()

            if (species.isNotBlank() && observationDate.isNotBlank()) {
                val observation = hashMapOf(
                    "species" to species,
                    "observationDate" to observationDate,
                    "notes" to notes,
                    "userId" to userId  // Add the userId field here
                )

                // Convert the capturedImage to a byte array
                val baos = ByteArrayOutputStream()
                capturedImage?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                // Create a child reference in Firebase Storage
                val imageRef = storageRef.child("images/${userId}/observation.jpg")

                // Upload the image to Firebase Storage
                val uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // You can also get the download URL
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Add the download URL to the observation map
                        observation["capturedImage"] = downloadUri.toString()

                        // Then you can save the observation to Firestore
                        db.collection("observations")
                            .add(observation)  // Use add() instead of set() to create a new document
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully written!")
                                val intent = Intent(this, PastObservationsActivity::class.java)
                                intent.putExtra("observation", observation)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                    }
                }
            } else {
                Log.d("Observation", "Missing information: species or date is not provided.")
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            capturedImage = data?.extras?.get("data") as? Bitmap
            if (capturedImage != null) {
                imageViewCapturedImage.setImageBitmap(capturedImage)
                imageViewCapturedImage.visibility = View.VISIBLE
            }
        }
    }


    companion object {
        private const val CAPTURE_IMAGE_REQUEST = 1
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
    }

}
