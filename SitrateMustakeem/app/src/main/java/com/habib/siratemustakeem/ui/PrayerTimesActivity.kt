package com.habib.siratemustakeem.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.databinding.ActivityPrayerTimesBinding
import com.habib.siratemustakeem.prayer.IslamicDateUtils
import com.habib.siratemustakeem.prayer.PrayerScheduleProvider
import com.habib.siratemustakeem.prayer.PrayerTimeCalculator
import com.habib.siratemustakeem.prayer.PrayerTimesResult
import kotlinx.coroutines.launch

class PrayerTimesActivity : AppCompatActivity() {

    private var binding: ActivityPrayerTimesBinding? = null
    private val locationPermissionRequestCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_prayer_times)

        binding?.toplayout?.tvTitle?.text = getString(R.string.title_prayer_times)
        binding?.toplayout?.backImage?.setOnClickListener { finish() }
        binding?.recyclerView?.layoutManager = LinearLayoutManager(this)
        binding?.tvDateLine?.text = "${IslamicDateUtils.getHijriDateUrdu()}   •   ${IslamicDateUtils.getGregorianDateUrdu()}"

        ensureLocationPermissionThenLoad()
    }

    private fun ensureLocationPermissionThenLoad() {
        val hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            loadTimes()
            return
        }

        when {
            !com.habib.siratemustakeem.prayer.LocationPermissionPrefs.hasAskedBefore(this) -> {
                com.habib.siratemustakeem.prayer.LocationPermissionPrefs.markAsked(this)
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    locationPermissionRequestCode
                )
            }
            !com.habib.siratemustakeem.prayer.LocationPermissionPrefs.hasShownRationaleBefore(this) -> {
                com.habib.siratemustakeem.prayer.LocationPermissionPrefs.markRationaleShown(this)
                showLocationRationaleDialog()
            }
            else -> loadTimes() // already asked + already explained once — don't nag further
        }
    }

    private fun showLocationRationaleDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_location_permission, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<TextView>(R.id.btnAllowLocation).setOnClickListener {
            dialog.dismiss()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationPermissionRequestCode
            )
        }
        dialogView.findViewById<TextView>(R.id.btnDenyLocation).setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, getString(R.string.location_denied_notice), Toast.LENGTH_LONG).show()
            loadTimes()
        }
        dialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode) {
            val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                Toast.makeText(this, getString(R.string.location_denied_notice), Toast.LENGTH_LONG).show()
            }
            // Whether granted or not, LocationHelper falls back gracefully
            // (last known location, then Lahore, Pakistan by default).
            loadTimes()
        }
    }

    private fun loadTimes() {
        binding?.progressBar?.visibility = View.VISIBLE
        lifecycleScope.launch {
            val schedule = PrayerScheduleProvider.getTodayTimes(this@PrayerTimesActivity)
            binding?.progressBar?.visibility = View.GONE
            binding?.tvDateLine?.text = "${IslamicDateUtils.getHijriDateUrdu()}   •   ${IslamicDateUtils.getGregorianDateUrdu()}\n${schedule.location.cityLabel}"
            binding?.recyclerView?.adapter = PrayerTimeListAdapter(buildRows(schedule.times))
        }
    }

    private fun buildRows(times: PrayerTimesResult): List<Pair<String, String>> = listOf(
        getString(R.string.prayer_fajr) to PrayerTimeCalculator.formatTime(times.fajr),
        getString(R.string.prayer_sunrise) to PrayerTimeCalculator.formatTime(times.sunrise),
        getString(R.string.prayer_dhuhr) to PrayerTimeCalculator.formatTime(times.dhuhr),
        getString(R.string.prayer_asr) to PrayerTimeCalculator.formatTime(times.asr),
        getString(R.string.prayer_maghrib) to PrayerTimeCalculator.formatTime(times.maghrib),
        getString(R.string.prayer_isha) to PrayerTimeCalculator.formatTime(times.isha)
    )

    private class PrayerTimeListAdapter(private val rows: List<Pair<String, String>>) :
        RecyclerView.Adapter<PrayerTimeListAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name: TextView = itemView.findViewById(R.id.tvPrayerName)
            val time: TextView = itemView.findViewById(R.id.tvPrayerTime)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item_prayer_time, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = rows[position].first
            holder.time.text = rows[position].second
        }

        override fun getItemCount(): Int = rows.size
    }
}
