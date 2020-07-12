package com.j2d2.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.j2d2.R

class MyPetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_setting_my_pat_title)
        setContentView(R.layout.activity_my_pet)
    }
}