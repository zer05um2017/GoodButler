package com.j2d2.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.j2d2.R
import kotlinx.android.synthetic.main.activity_my_pet.*

class MyPetActivity : AppCompatActivity(), OnListClickListener {
    lateinit var selectedBreed: ItemList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_setting_my_pat_title)
        setContentView(R.layout.activity_my_pet)

        textBreedSelection.setOnClickListener {
            val dlg = PopupBreedSelectionDialog(this@MyPetActivity, this)
            dlg.start()
        }
    }

    override fun onSelected(selected: ItemList) {
        textBreedSelection.text = selected.titleName
        selectedBreed = selected
    }
}