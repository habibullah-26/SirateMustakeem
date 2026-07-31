package com.habib.siratemustakeem.prayer

import android.content.Context

/**
 * Remembers what we've already done around the location permission flow, so we don't repeat
 * ourselves every time the app opens:
 * - The very first time, we ask directly via the system permission dialog (no extra explanation
 *   needed yet — that's the standard, expected first-run behaviour).
 * - If that's ever denied, the *next* time we show our own bilingual explanation dialog once
 *   before asking again — but only once automatically; after that we stop prompting on our own
 *   and just proceed with the Lahore default silently, respecting the user's choice.
 */
object LocationPermissionPrefs {

    private const val PREFS_NAME = "location_permission_prefs"
    private const val KEY_ASKED_ONCE = "asked_once"
    private const val KEY_RATIONALE_SHOWN_ONCE = "rationale_shown_once"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun hasAskedBefore(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ASKED_ONCE, false)

    fun markAsked(context: Context) {
        prefs(context).edit().putBoolean(KEY_ASKED_ONCE, true).apply()
    }

    fun hasShownRationaleBefore(context: Context): Boolean =
        prefs(context).getBoolean(KEY_RATIONALE_SHOWN_ONCE, false)

    fun markRationaleShown(context: Context) {
        prefs(context).edit().putBoolean(KEY_RATIONALE_SHOWN_ONCE, true).apply()
    }
}
