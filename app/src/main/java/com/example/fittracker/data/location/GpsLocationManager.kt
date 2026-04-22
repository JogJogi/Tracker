package com.example.fittracker.data.location

import android.content.Context
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * FOSS-compatible GPS wrapper using AOSP LocationManager.
 * No Google Play Services / FusedLocationProvider.
 *
 * Filters points with accuracy > [accuracyThresholdM] (default 50m).
 */
class GpsLocationManager(context: Context) {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val isGpsEnabled: Boolean
        get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    /**
     * Returns a flow of GPS locations, filtered by accuracy.
     * Requests updates every 1 second with 0 min distance (captures standstill).
     */
    @Suppress("MissingPermission")
    fun locationFlow(accuracyThresholdM: Float = 50f): Flow<Location> = callbackFlow {
        val listener = LocationListener { location ->
            if ((location.accuracy <= accuracyThresholdM)) {
                trySend(location)
            }
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1_000L,    // minTimeMs
            0f,        // minDistanceM — 0 to capture standstill trackpoints
            listener
        )

        // Register GNSS status so Android shows the GPS icon in status bar
        val gnssCallback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            object : GnssStatus.Callback() {
                override fun onStarted() = Unit
                override fun onStopped() = Unit
                override fun onFirstFix(ttffMillis: Int) = Unit
                override fun onSatelliteStatusChanged(status: GnssStatus) = Unit
            }.also { locationManager.registerGnssStatusCallback(it, null) }
        } else null

        awaitClose {
            locationManager.removeUpdates(listener)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                gnssCallback?.let { locationManager.unregisterGnssStatusCallback(it) }
            }
        }
    }

    /**
     * Returns a single last-known location for initial map centering.
     */
    @Suppress("MissingPermission")
    fun getLastKnownLocation(): Location? =
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
}
