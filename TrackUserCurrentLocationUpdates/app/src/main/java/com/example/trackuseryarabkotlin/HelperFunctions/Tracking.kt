package com.example.trackuseryarabkotlin.HelperFunctions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trackuseryarabkotlin.BackGroundCurrentLocationTracker.LocationBroadcastReceiver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest

class Tracking {

    companion object {

        val REQUEST_LOCATION_PERMISSION = 1                             // M Osama: constant for the permission req. code

        /* M Osama: returns lastTrackedLocation incase User granted permissions*/
        fun getCurrentLocation(fusedLocationClient: FusedLocationProviderClient,context: Context) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let { Toast.makeText(context, "Latitude: ${location.latitude}, Longitude: ${location.longitude}", Toast.LENGTH_SHORT).show() }
                }
            }
        }

        /* M Osama: track LocationUpdates event if the app isn't running*/
        fun getLocationBroadCastReceiverIntent(context: Context) = LocationBroadcastReceiver.getPendingIntent(context)

        /* M Osama: locationRequest parameters */
        fun getLocationRequest() = LocationRequest.create().apply {
            interval = 5000                                     // M Osama: LocationUpdates interval = 5 seconds
            fastestInterval = 3000                              // M Osama: fastest LocationUpdates interval = 3 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY   // M Osama: LocationUpdates of high Accuracy
            smallestDisplacement = 1f                           // M Osama: Min triggered displacement is 1 meter
        }

        /* M Osama: requestLocationUpdate every 10sec by most */
        @SuppressLint("MissingPermission")
        fun requestLocationUpdates(fusedLocationClient:FusedLocationProviderClient,context: Context,handler: android.os.Handler) {
            fusedLocationClient.requestLocationUpdates(getLocationRequest(), getLocationBroadCastReceiverIntent(context))

            /* M Osama: Every 10 seconds this object this object calls getCurrentLocation() ;*/
            val locationRunnable = object : Runnable {
                override fun run() {
                    getCurrentLocation(fusedLocationClient, context)
                    handler.postDelayed(this, 10000)
                }
            }

            handler.postDelayed(locationRunnable, 10000)    /* M Osama: start the timer */
        }

        /* M Osama: checkPermissions for every the app was destroyed & started again to run*/
        fun checkPermissions(fusedLocationClient: FusedLocationProviderClient,context: Context,handler: android.os.Handler,activity:Activity){
            /* If permissions isn't granted; ask the user for permissions
            If permissions is already granted; start requesting location updates */
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION) }
            else { requestLocationUpdates(fusedLocationClient,context,handler) }
        }

    }





}