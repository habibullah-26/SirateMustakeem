package com.habib.siratemustakeem.ui

//import com.habib.siratemustakeem.databinding.ActivitySplashBinding
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.habib.siratemustakeem.R
import com.habib.siratemustakeem.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {
    //    private val SPLASHDISPLAYLENGTH = 1000L
    private var binding: ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//    setContentView(R.layout.activity_splash)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)


        // val backgroundImage: ImageView = findViewById(R.id.SplashScreenImage)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_slide)
        binding?.imageLogo?.startAnimation(slideAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            val mainIntent = Intent(this, DashBoardActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, 2000L)
    }


}