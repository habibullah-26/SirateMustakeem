package com.habib.siratemustakeem.prayer

import android.content.Context

/**
 * Tracks whether we've ever asked for location permission before, so callers can decide
 * between:
 *  - First time ever: request the system permission directly (no extra dialog needed yet —
 *    the system prompt itself is the first ask).
 *  - Asked before and still not granted: show our own explanatory dialog first (Android's
 *    recommended pattern via shouldShowRequestPermissionRationale), since a bare re-prompt
 *    with no context is more likely to be declined again.
 *  - Already granted: never shown again, anywhere.
 */
object LocationPermissionManager {

    private const val PREFS_NAME = "location_permission_prefs"
    private const val KEY_HAS_ASKED_BEFORE = "has_asked_before"

    fun hasAskedBefore(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_HAS_ASKED_BEFORE, false)

    fun markAsked(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_HAS_ASKED_BEFORE, true)
            .apply()
    }
}
