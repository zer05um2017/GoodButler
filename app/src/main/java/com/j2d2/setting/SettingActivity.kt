package com.j2d2.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.j2d2.R
import com.j2d2.main.PopupMessageDialog
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_main_setting_title)
        setContentView(R.layout.activity_setting)
        btnLicense.setOnClickListener(this)
        // image license
        // <div>아이콘 제작자
        // 아이콘 제작자 <a href="https://www.flaticon.com/kr/authors/freepik" title="Freepik">Freepik</a>
        // from <a href="https://www.flaticon.com/kr/" title="Flaticon"> www.flaticon.com</a>
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnLicense -> {
                val dlg = PopupLicenseDialog(this)
                dlg.start(getString(R.string.delete_message))
            }
        }
    }
}