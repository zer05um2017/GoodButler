package com.j2d2.main

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.j2d2.insulin.Insulin
import com.j2d2.insulin.InsulinDao

class MainApp: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPref.init(this)
    }
}

