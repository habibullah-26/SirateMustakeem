package com.habib.siratemustakeem.prayer

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

/** Where a [ResolvedLocation] actually came from — used to decide what to tell the user. */
enum class LocationSource { DEVICE_GPS_OR_NETWORK, DEFAULT_FALLBACK }

data class ResolvedLocation(
    val latitude: Double,
    val longitude: Double,
    val source: LocationSource,
    val cityLabel: String
)

object LocationHelper {

    // Default fallback when location truly can't be determined (denied permission, no
    // provider available, or the device never returns a fix in time): Lahore, Pakistan —
    // matches this app's primary audience so the default prayer times are still meaningful
    // rather than off by hours for a mismatched city/timezone combination.
    private const val FALLBACK_LAT = 31.5204
    private const val FALLBACK_LNG = 74.3587
    private const val FALLBACK_CITY_LABEL = "لاہور، پاکستان"

    private const val LOCATION_FETCH_TIMEOUT_MS = 6000L

    @SuppressLint("MissingPermission")
    suspend fun resolveLocation(context: Context): ResolvedLocation {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val fix = manager?.let { getBestAvailableFix(context, it) }

        if (fix != null) {
            val cityLabel = reverseGeocode(context, fix.latitude, fix.longitude) ?: FALLBACK_CITY_LABEL
            return ResolvedLocation(fix.latitude, fix.longitude, LocationSource.DEVICE_GPS_OR_NETWORK, cityLabel)
        }

        return ResolvedLocation(FALLBACK_LAT, FALLBACK_LNG, LocationSource.DEFAULT_FALLBACK, FALLBACK_CITY_LABEL)
    }

    @SuppressLint("MissingPermission")
    private suspend fun getBestAvailableFix(context: Context, manager: LocationManager): Location? {
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        var best: Location? = null
        for (provider in providers) {
            try {
                if (manager.isProviderEnabled(provider)) {
                    val loc = manager.getLastKnownLocation(provider)
                    if (loc != null && (best == null || loc.accuracy < best!!.accuracy)) best = loc
                }
            } catch (e: SecurityException) {
                // permission not granted — fall through
            }
        }
        if (best != null) return best

        // No cached fix yet — try one fresh update, but never wait more than a few seconds;
        // otherwise a device that never gets a GPS lock (common indoors/on emulators) would
        // hang this call forever instead of falling back.
        return try {
            withTimeoutOrNull(LOCATION_FETCH_TIMEOUT_MS) { requestSingleUpdate(manager) }
        } catch (e: Exception) {
            null
        }
    }

    private fun reverseGeocode(context: Context, lat: Double, lng: Double): String? {
        return try {
            @Suppress("DEPRECATION")
            val results = Geocoder(context, Locale.getDefault()).getFromLocation(lat, lng, 1)
            val place = results?.firstOrNull() ?: return null
            listOfNotNull(place.locality ?: place.subAdminArea, place.countryName)
                .joinToString("، ")
                .ifBlank { null }
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestSingleUpdate(manager: LocationManager): Location? =
        suspendCancellableCoroutine { cont ->
            val provider = when {
                manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                else -> null
            }
            if (provider == null) {
                cont.resume(null)
                return@suspendCancellableCoroutine
            }
            val listener = object : android.location.LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (cont.isActive) cont.resume(location)
                    manager.removeUpdates(this)
                }
                @Deprecated("Deprecated in API 29+, harmless no-op override for older devices")
                override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {
                    if (cont.isActive) cont.resume(null)
                }
            }
            try {
                manager.requestSingleUpdate(provider, listener, android.os.Looper.getMainLooper())
            } catch (e: SecurityException) {
                cont.resume(null)
            }
            cont.invokeOnCancellation { manager.removeUpdates(listener) }
        }
}
