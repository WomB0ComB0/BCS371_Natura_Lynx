package com.example.natura_lynx

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class NaturaLynxApplication : Application() {
    companion object {
        lateinit var instance: NaturaLynxApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
    }
} 