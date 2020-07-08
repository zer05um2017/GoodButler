package com.j2d2.main

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.j2d2.R

class PopupMessageDialog(context : Context, listener: DialogOnClickListener) {
    private val dlg = Dialog(context)   //부모 액티비티의 context 가 들어감
    private lateinit var lblDesc : TextView
    private lateinit var btnOK : Button
    private lateinit var btnCancel : Button
    private val listener = listener
//    private lateinit var listener : MyDialogOKClickedListener

    fun start(content : String) {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.popup_dialog)     //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        lblDesc = dlg.findViewById(R.id.content)
        lblDesc.text = content

        btnOK = dlg.findViewById(R.id.ok)
        btnOK.setOnClickListener {
            listener.OnPositiveClick()
            dlg.dismiss()
        }

        btnCancel = dlg.findViewById(R.id.cancel)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }
//    fun setOnOKClickedListener(listener: (String) -> Unit) {
//        this.listener = object: MyDialogOKClickedListener {
//            override fun onOKClicked(content: String) {
//                listener(content)
//            }
//        }
//    }
//
//    interface MyDialogOKClickedListener {
//        fun onOKClicked(content : String)
//    }
}

