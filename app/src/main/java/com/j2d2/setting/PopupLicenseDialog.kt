package com.j2d2.setting

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.Window
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import com.j2d2.R
import com.j2d2.main.DialogOnClickListener

class PopupLicenseDialog (context : Context){
    private val dlg = Dialog(context)   //부모 액티비티의 context 가 들어감
//    private lateinit var lblLicense : TextView
    private lateinit var btnOK : Button
    private lateinit var wv : WebView

    fun start(content : String) {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.popup_license_dialog)     //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        wv = dlg.findViewById(R.id.webView)
        wv.loadUrl("file:///android_asset/icon_license.htm")
        btnOK = dlg.findViewById(R.id.ok)
        btnOK.setOnClickListener {
            dlg.dismiss()
        }

        dlg?.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog?.dismiss()
                    return true
                }
                return false
            }

        })

        dlg.show()
    }
}