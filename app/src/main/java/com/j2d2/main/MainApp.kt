package com.j2d2.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.multidex.MultiDexApplication
import com.j2d2.R

class MainApp: MultiDexApplication() {

    companion object {
        private var selectedPetId: Long = 0

        fun getSelectedPetId(): Long {
            return selectedPetId
        }

        fun setSelectedPetId(id:Long) {
            selectedPetId = id
        }
    }

//    @RequiresApi(Build.VERSION_CODES.M)
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

