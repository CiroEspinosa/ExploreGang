package com.example.exploregang.application

import android.app.Application
import com.example.exploregang.util.NotificationUtils
import com.google.firebase.FirebaseApp

class ExploreGangApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)
        NotificationUtils.createNotificationChannel(applicationContext)
    }
}