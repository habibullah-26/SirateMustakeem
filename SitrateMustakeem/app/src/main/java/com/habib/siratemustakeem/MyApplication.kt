package com.habib.siratemustakeem

import android.app.Application
import android.content.Context
import com.habib.siratemustakeem.MyApplication

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}