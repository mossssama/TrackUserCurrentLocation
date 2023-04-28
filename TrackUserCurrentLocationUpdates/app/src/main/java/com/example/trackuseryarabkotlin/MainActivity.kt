package com.example.trackuseryarabkotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trackuseryarabkotlin.BackGroundCurrentLocationTracker.LocationBroadcastReceiver
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_LOCATION_PERMISSION = 1                             // M Osama: constant for the permission req. code
    private lateinit var fusedLocationClient: FusedLocationProviderClient   // M Osama: used to return user location
    private lateinit var handler: Handler                                   // M Osama: handler that runs a timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        handler = Handler()

        /* If permissions isn't granted; ask the user for permissions
           If permissions is already granted; start requesting location updates */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION) }
        else { requestLocationUpdates() }

    }

        /* If permissions is refused; fire a toast
           If permissions is granted; start requesting location updates */  /* M Osama: executed only the first time the app is runned */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) { requestLocationUpdates() }
            else { Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show() }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(getLocationRequest(), getPendingIntent())

        /* M Osama: Every 10 seconds this object this object calls getCurrentLocation() ;*/
        val locationRunnable = object : Runnable {
            override fun run() {
                getCurrentLocation()
                handler.postDelayed(this, 10000)
            }
        }

        handler.postDelayed(locationRunnable, 10000)    /* M Osama: start the timer */
    }

    private fun getLocationRequest() = LocationRequest.create().apply {
        interval = 5000                                     // M Osama: LocationUpdates interval = 5 seconds
        fastestInterval = 3000                              // M Osama: fastest LocationUpdates interval = 3 seconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY   // M Osama: LocationUpdates of high Accuracy
        smallestDisplacement = 1f                           // M Osama: Min triggered displacement is 1 meter
    }

    /* M Osama: track LocationUpdates event if the app isn't running*/
    private fun getPendingIntent() = LocationBroadcastReceiver.getPendingIntent(this)

    /* M Osama: returns lastTrackedLocation incase User granted permissions*/
    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let { Toast.makeText(this, "Latitude: ${location.latitude}, Longitude: ${location.longitude}", Toast.LENGTH_SHORT).show() }
            }
        }
    }

}
