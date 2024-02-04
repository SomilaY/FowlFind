package com.FowlFind.googlemaps

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.FowlFind.googlemaps.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

    private lateinit var hotspotImageView: ImageView
    private lateinit var listImageView: ImageView
    private lateinit var profileImageView: ImageView
    private var maxDistance: Float = 10f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val fabchat: FloatingActionButton = findViewById(R.id.chatFab)
        fabchat.setOnClickListener {
            val intent = Intent(this, Community::class.java)
            startActivity(intent)
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        getCurrentLocationUser()

        val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        maxDistance = sharedPrefs.getFloat("max_distance", 10f)

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

    private fun getCurrentLocationUser() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )
            return
        }

        val getLocation =
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLocation = location
                    Toast.makeText(
                        applicationContext, currentLocation.latitude.toString() + "" +
                                currentLocation.longitude.toString(), Toast.LENGTH_LONG
                    ).show()

                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED

            ) {

                getCurrentLocationUser()
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (::currentLocation.isInitialized) {
            val latlng = LatLng(currentLocation.latitude, currentLocation.longitude)
            val markerOptions = MarkerOptions().position(latlng).title("Current Location")

            googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latlng))
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 7f))
            googleMap?.addMarker(markerOptions)

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.ebird.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create an instance of EbirdService
            val service = retrofit.create(EbirdService::class.java)

            // Call getHotspots and handle the response
            service.getHotspots(currentLocation.latitude, currentLocation.longitude, maxDistance).enqueue(object : Callback<List<Hotspot>> {
                override fun onResponse(call: Call<List<Hotspot>>, response: Response<List<Hotspot>>) {
                    if (response.isSuccessful) {
                        val hotspots = response.body()
                        if (hotspots != null) {
                            Log.d("Bird", "Number of hotspots: ${hotspots.size}")
                            for (hotspot in hotspots) {
                                val hotspotLatLng = LatLng(hotspot.lat, hotspot.lng)
                                googleMap?.addMarker(
                                    MarkerOptions()
                                        .position(hotspotLatLng)
                                        .title("Hotspot")
                                        .icon(BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_BLUE)))

                                googleMap.setOnMarkerClickListener { marker ->
                                    if (marker.title == "Hotspot") {
                                        // Extract the latitude and longitude of the selected hotspot marker
                                        val hotspotLatLng = marker.position

                                        // Call the function to display directions to the hotspot
                                        displayDirectionsToHotspot(latlng, hotspotLatLng)

                                        true
                                    } else {
                                        false
                                    }}
                            }

                        } else {
                            Log.d("Bird", "Response body is null")
                        }
                    } else {
                        Log.d("Bird", "Response is not successful. Code: ${response.code()}, Message: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<Hotspot>>, t: Throwable) {
                    Log.d("Bird", "Failed to get hotspots", t)
                }
            })
        } else {
            Log.d("MapsActivity", "Current location is not initialized")
        }
    }

    private fun displayDirectionsToHotspot(userLocation: LatLng, hotspotLatLng: LatLng) {
        val uri = "http://maps.google.com/maps?saddr=${userLocation.latitude},${userLocation.longitude}&daddr=${hotspotLatLng.latitude},${hotspotLatLng.longitude}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps") // Ensure Google Maps app is used
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            // Handle the case where Google Maps is not installed
            Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_SHORT).show()
        }
    }

}




