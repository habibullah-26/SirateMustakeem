package com.habib.siratemustakeem.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory
//import com.google.android.play.core.install.model.AppUpdateType
//import com.google.android.play.core.install.model.UpdateAvailability
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.databinding.ActivityDashboardBinding
import com.habib.siratemustakeem.models.Duwa
import com.habib.siratemustakeem.utils.Util

class DashBoardActivity : AppCompatActivity() {
    var binding: ActivityDashboardBinding? = null
    var duwaItem: Duwa? = null
    val MY_REQUEST_CODE = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        binding?.cardView1?.setOnClickListener{
            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.putExtra("title",getString(R.string.title_duwain))
            startActivity(mainIntent)
        }
        binding?.cardView2?.setOnClickListener{
            val mainIntent = Intent(this, KalmaActivity::class.java)
            mainIntent.putExtra("title",getString(R.string.title_kalimat))
            startActivity(mainIntent)
        }

        binding?.cardView3?.setOnClickListener {
            val duwasList = Util.getAytualKursiFromAssets()
            if (duwasList.size > 0) {
                val intent = Intent(this, DuwaDetail::class.java)
                intent.putExtra("data", duwasList.get(0))
                intent.putExtra("title",getString(R.string.title_ayat_alkursi))
                startActivity(intent)
            }
        }
        binding?.cardView4?.setOnClickListener {
            val duwasList = Util.getQunootFromAssets()
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
    }

    fun updateApp(){
//        val appUpdateManager = AppUpdateManagerFactory.create(this)
//
//// Returns an intent object that you use to check for an update.
//        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
//
//// Checks that the platform will allow the specified type of update.
//        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                // This example applies an immediate update. To apply a flexible update
//                // instead, pass in AppUpdateType.FLEXIBLE
//                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
//            ) {
//
//
//                appUpdateManager.startUpdateFlowForResult(
//                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
//                    appUpdateInfo,
//                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
//                    AppUpdateType.IMMEDIATE,
//                    // The current activity making the update request.
//                    this,
//                    // Include a request code to later monitor this update request.
//                    MY_REQUEST_CODE)
//            }
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }
}