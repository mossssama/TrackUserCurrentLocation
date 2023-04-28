package com.example.trackuseryarabkotlin

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackuseryarabkotlin.HelperFunctions.Tracking.Companion.REQUEST_LOCATION_PERMISSION
import com.example.trackuseryarabkotlin.HelperFunctions.Tracking.Companion.checkPermissions
import com.example.trackuseryarabkotlin.HelperFunctions.Tracking.Companion.requestLocationUpdates
import com.google.android.gms.location.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient   // M Osama: used to return user location
    private lateinit var handler: Handler                                   // M Osama: handler that runs a timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* M Osama: tracking user's latLng */
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        handler = Handler()
        checkPermissions(fusedLocationClient, applicationContext,handler,this)

    }

        /* If permissions is refused; fire a toast
           If permissions is granted; start requesting location updates */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) { requestLocationUpdates(fusedLocationClient,applicationContext,handler) }
            else { Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show() }
        }
    }

}
