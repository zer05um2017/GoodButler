package com.j2d2.main

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.j2d2.R
import com.j2d2.insulin.Insulin
import com.j2d2.insulin.InsulinDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainApp: Application() {

    companion object {
        private var selectedPetId: Long = 0

        fun getSelectedPetId(): Long {
            return selectedPetId
        }

        fun setSelectedPetId(id:Long) {
            selectedPetId = id
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        SharedPref.init(this)

        with(SharedPref.prefs) {
            if (contains(R.string.com_j2d2_petinfo_pet_selected_id.toString())) {
                selectedPetId = getLong(R.string.com_j2d2_petinfo_pet_selected_id.toString(), 0)
            }
        }
    }
}

