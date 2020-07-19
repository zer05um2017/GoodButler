package com.j2d2.main

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.j2d2.R

object SharedPref {
    lateinit var prefs: SharedPreferences
    private const val PREPERANCE_FILE = "SharedPreferenceInfo"

    private fun isOverMashmellow(): Boolean {
        if(Build.VERSION.SDK_INT >= 23) {
            return true
        }
        return false
    }

//    @RequiresApi(Build.VERSION_CODES.M)
@RequiresApi(Build.VERSION_CODES.M)
fun init(context: Context) {
        if(isOverMashmellow()) {
            val sharedEnPreferences: SharedPreferences by lazy {
                val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
                val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

                EncryptedSharedPreferences.create(
                    PREPERANCE_FILE,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            }
            prefs = sharedEnPreferences
        } else {
            val sharedPreferences: SharedPreferences by lazy {
                context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
            }
            prefs = sharedPreferences
        }
    }

//    var data: String
//        get() = preferences.getString(DATA, "") ?: ""
//        set(value) = preferences.edit().putString(DATA, value)
}