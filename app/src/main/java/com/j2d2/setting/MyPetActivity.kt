package com.j2d2.setting

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.j2d2.R
import kotlinx.android.synthetic.main.activity_blood_glucose.*
import kotlinx.android.synthetic.main.activity_my_pet.*
import java.text.SimpleDateFormat
import java.util.*

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

        setDateTimeListener()

    }

    override fun onSelected(selected: ItemList) {
        textBreedSelection.text = selected.titleName
        selectedBreed = selected
    }

    private fun setDateTimeListener() {
        textBornDate.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    var y: Int
                    var m: Int
                    var d: Int
                    val cal = GregorianCalendar.getInstance()
                    y = cal.get(Calendar.YEAR)
                    m = cal.get(Calendar.MONTH)
                    d = cal.get(Calendar.DAY_OF_MONTH)

                    val datepickerdialog: DatePickerDialog = DatePickerDialog(
                        this@MyPetActivity,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            val myFormat = "yyyy-MM-dd" // mention the format you need
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            cal.set(year, monthOfYear, dayOfMonth)
                            textBornDate!!.setText(sdf.format(cal.time))
                        },
                        y,
                        m,
                        d
                    )

                    datepickerdialog.show()
                    false
                }
            }
            true
        }

        textGotSickDate.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    var y: Int
                    var m: Int
                    var d: Int
                    val cal = GregorianCalendar.getInstance()
                    y = cal.get(Calendar.YEAR)
                    m = cal.get(Calendar.MONTH)
                    d = cal.get(Calendar.DAY_OF_MONTH)

                    val datepickerdialog: DatePickerDialog = DatePickerDialog(
                        this@MyPetActivity,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            val myFormat = "yyyy-MM-dd" // mention the format you need
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            cal.set(year, monthOfYear, dayOfMonth)
                            textGotSickDate!!.setText(sdf.format(cal.time))
                        },
                        y,
                        m,
                        d
                    )

                    datepickerdialog.show()
                    false
                }
            }
            true
        }
    }
}