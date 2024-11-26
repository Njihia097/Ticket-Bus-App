package com.example.tickectbusbooking_app

import android.app.Application
import com.example.tickectbusbooking_app.database.AppDatabase
import com.example.tickectbusbooking_app.util.DataInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TicketBusApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getDatabase(this)
        DataInitializer.populateData(database)
    }
}