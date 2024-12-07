package com.example.natura_lynx

import android.app.Application

class NaturaLynxApplication : Application() {
    companion object {
        lateinit var instance: NaturaLynxApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
} 