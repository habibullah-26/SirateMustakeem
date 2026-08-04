package com.habib.siratemustakeem.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.databinding.ActivityDashboardBinding
import com.habib.siratemustakeem.models.Duwa
import com.habib.siratemustakeem.prayer.IslamicDateUtils
import com.habib.siratemustakeem.prayer.LocationSource
import com.habib.siratemustakeem.prayer.PrayerScheduleProvider
import com.habib.siratemustakeem.utils.JsonUtils
import kotlinx.coroutines.launch
import java.util.ArrayList

class DashBoardActivity : AppCompatActivity() {
    var binding: ActivityDashboardBinding? = null
    var duwaItem: Duwa? = null
    val MY_REQUEST_CODE = 200
    private val locationPermissionRequestCode = 102

    val fileName_duwequnoot: String = "duwequnoot.json"
    val fileName_ayatkursi: String = "ayatkursi.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)

        //Quran majeed
        binding?.cardView1?.setOnClickListener{
            startActivity(Intent(this, QuranHomeActivity::class.java))
        }
        //Rabna Duwain
        binding?.cardView2?.setOnClickListener{
            val mainIntent = Intent(this, RabnaActivity::class.java)
            mainIntent.putExtra("title",getString(R.string.title_duwain_rabna))
            startActivity(mainIntent)
        }

        //Adith Feed
        binding?.cardView3?.setOnClickListener {
            startActivity(Intent(this, FeedActivity::class.java))
        }

        //Duwain
        binding?.cardView4?.setOnClickListener {
//            val list = JsonUtils.getListDuwa(this, fileName_duwequnoot)
//            val duwasList = ArrayList(list)
//            if (duwasList.size > 0) {
//                val intent = Intent(this, DuwaDetail::class.java)
//                intent.putExtra("data", duwasList.get(0))
//                intent.putExtra("title",getString(R.string.title_qua_qunoot))
//                startActivity(intent)
//            }
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.putExtra("title",getString(R.string.title_duwain))
            startActivity(mainIntent)
        }

//Kalamats
        binding?.cardView5?.setOnClickListener {
            val mainIntent = Intent(this, KalmaActivity::class.java)
            mainIntent.putExtra("title",getString(R.string.title_kalimat))
            startActivity(mainIntent)
        }

//Ayat Kursi
        binding?.cardView6?.setOnClickListener {
//            startActivity(Intent(this, QuranHomeActivity::class.java))
            val list = JsonUtils.getListDuwa(this, fileName_ayatkursi)
            val duwasList = ArrayList(list)
            if (duwasList.size > 0) {
                val intent = Intent(this, DuwaDetail::class.java)
                intent.putExtra("data", duwasList.get(0))
                intent.putExtra("title",getString(R.string.title_ayat_alkursi))
                startActivity(intent)
            }
        }

        //Due Qonoot
        binding?.cardView7?.setOnClickListener {
//            startActivity(Intent(this, FeedActivity::class.java))
            val list = JsonUtils.getListDuwa(this, fileName_duwequnoot)
            val duwasList = ArrayList(list)
            if (duwasList.size > 0) {
                val intent = Intent(this, DuwaDetail::class.java)
                intent.putExtra("data", duwasList.get(0))
                intent.putExtra("title",getString(R.string.title_qua_qunoot))
                startActivity(intent)
            }
        }

        binding?.contactUsBtn?.setOnClickListener {
            val intent = Intent(this, ContactUsActivity::class.java)
            startActivity(intent)
        }

        binding?.btnSearch?.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding?.cardNextPrayer?.setOnClickListener {
            startActivity(Intent(this, PrayerTimesActivity::class.java))
        }

        binding?.tvDashboardDate?.text =
            "${IslamicDateUtils.getHijriDateUrdu()}   •   ${IslamicDateUtils.getGregorianDateUrdu()}"

        maybeRequestLocationThenLoad()
    }

    /**
     * First time ever: ask directly via the system permission prompt, no extra explanation —
     * that's the standard, expected first-run behaviour and doesn't need justifying up front.
     * If that was denied before, the next time shows our own bilingual explanation dialog once
     * before asking again. After that, we stop prompting automatically and just proceed with
     * the Lahore default — respecting whatever the user already decided instead of nagging on
     * every launch.
     */
    private fun maybeRequestLocationThenLoad() {
        val hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            loadNextPrayer()
            return
        }

        when {
            !com.habib.siratemustakeem.prayer.LocationPermissionPrefs.hasAskedBefore(this) -> {
                com.habib.siratemustakeem.prayer.LocationPermissionPrefs.markAsked(this)
                requestSystemLocationPermission()
            }
            !com.habib.siratemustakeem.prayer.LocationPermissionPrefs.hasShownRationaleBefore(this) -> {
                com.habib.siratemustakeem.prayer.LocationPermissionPrefs.markRationaleShown(this)
                showLocationRationaleDialog()
            }
            else -> loadNextPrayer() // already asked + already explained once — don't nag further
        }
    }

    private fun requestSystemLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            locationPermissionRequestCode
        )
    }

    private fun showLocationRationaleDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_location_permission, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<android.widget.TextView>(R.id.btnAllowLocation).setOnClickListener {
            dialog.dismiss()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationPermissionRequestCode
            )
        }
        dialogView.findViewById<android.widget.TextView>(R.id.btnDenyLocation).setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, getString(R.string.location_denied_notice), Toast.LENGTH_LONG).show()
            loadNextPrayer()
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
            // Whether granted or not, PrayerScheduleProvider/LocationHelper falls back
            // gracefully (last known location, then Lahore, Pakistan by default).
            loadNextPrayer()
        }
    }

    private fun loadNextPrayer() {
        lifecycleScope.launch {
            try {
                val schedule = PrayerScheduleProvider.getTodayTimes(this@DashBoardActivity)
                val current = PrayerScheduleProvider.getCurrentAndNextPrayer(schedule.times)
                binding?.tvNextPrayerLabel?.text = "${getString(R.string.current_prayer_prefix)} ${current.currentNameUrdu}"
                binding?.tvNextPrayerCountdown?.text =
                    "${getString(R.string.next_prayer_prefix)} ${current.nextNameUrdu} — ${current.nextCountdown}"
                binding?.tvNextPrayerTime?.text = current.currentTimeFormatted

                val locationSuffix = if (schedule.location.source == LocationSource.DEFAULT_FALLBACK) {
                    " (${getString(R.string.location_default_suffix)})"
                } else ""
                binding?.tvLocationLabel?.text = schedule.location.cityLabel + locationSuffix
            } catch (e: Exception) {
                binding?.tvNextPrayerLabel?.text = getString(R.string.title_prayer_times)
                binding?.tvNextPrayerCountdown?.text = ""
            }
        }
    }

    fun updateApp(){
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
            }
        }
    }
}
