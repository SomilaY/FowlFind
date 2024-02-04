package com.FowlFind.googlemaps

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class Community : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private lateinit var feedAdapter: FeedAdapter
    private val feedItems = mutableListOf<FeedItem>()
    private lateinit var hotspotImageView: ImageView
    private lateinit var listImageView: ImageView
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.community)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        feedAdapter = FeedAdapter(feedItems)
        recyclerView.adapter = feedAdapter

        val fabAddPhoto: FloatingActionButton = findViewById(R.id.fabAddPhoto)

        fabAddPhoto.setOnClickListener {
            showBottomSheet()
        }

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

    override fun onResume() {
        super.onResume()
        updateFeed()
    }

    private fun showBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        val btnUploadImage: Button = bottomSheetView.findViewById(R.id.btnUploadImage)
        val editTextComment: EditText = bottomSheetView.findViewById(R.id.editTextComment)
        val btnSubmit: Button = bottomSheetView.findViewById(R.id.btnSubmit)

        btnUploadImage.setOnClickListener {
            chooseImage()
        }

        btnSubmit.setOnClickListener {
            val comment = editTextComment.text.toString()
            if (filePath != null && comment.isNotBlank()) {
                val storageReference = FirebaseStorage.getInstance().reference
                val filePathInStorage = storageReference.child("images/${UUID.randomUUID()}")
                val uploadTask = filePathInStorage.putFile(filePath!!)

                uploadTask.addOnSuccessListener {
                    filePathInStorage.downloadUrl.addOnSuccessListener { uri ->
                        val db = FirebaseFirestore.getInstance()
                        val feedItem = hashMapOf(
                            "imageUri" to uri.toString(),
                            "comment" to comment
                        )
                        db.collection("feed").add(feedItem).addOnSuccessListener {
                            // Fetch the data from Firestore and update the RecyclerView
                            Log.d("Firestore", "DocumentSnapshot added")
                            db.collection("feed")
                                .get()
                                .addOnSuccessListener { result ->
                                    Log.d(TAG, "Documents: ${result.documents}")
                                    feedItems.clear() // Clear the old items
                                    for (document in result) {
                                        val imageUri = Uri.parse(document.data["imageUri"].toString())
                                        val comment = document.data["comment"].toString()
                                        feedItems.add(FeedItem(imageUri, comment))
                                    }
                                    feedAdapter.notifyDataSetChanged()
                                    Log.d("Firestore", "Adapter notified")
                                }
                                .addOnFailureListener { exception ->
                                    Log.w("Firestore", "Error getting documents.", exception)
                                    Log.w("Firestore", "Error getting documents.", exception)
                                    Log.w("Firestore", "Error getting documents.", exception)
                                }
                        }
                    }
                }
            }

            bottomSheetDialog.dismiss()
        }



        bottomSheetDialog.show()
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            // You now have the Uri of the image file, you can preview it or upload it to your server
        }
    }

    private fun updateFeed() {
        val db = FirebaseFirestore.getInstance()
        db.collection("feed")
            .get()
            .addOnSuccessListener { result ->
                feedItems.clear() // Clear the old items
                for (document in result) {
                    val imageUri = Uri.parse(document.data["imageUri"].toString())
                    val comment = document.data["comment"].toString()
                    feedItems.add(FeedItem(imageUri, comment))
                }
                feedAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
}