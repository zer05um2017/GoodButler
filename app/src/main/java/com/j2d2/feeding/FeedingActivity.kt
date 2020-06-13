package com.j2d2.feeding

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.j2d2.R
import com.j2d2.insulin.AppDatabase
import com.j2d2.insulin.Insulin
import kotlinx.android.synthetic.main.activity_feeding.*
import kotlinx.android.synthetic.main.activity_insulin.*
import kotlinx.android.synthetic.main.activity_insulin.editTextDate
import kotlinx.android.synthetic.main.activity_insulin.editTextTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.activity_insulin.btnInsert as btnInsert1

class FeedingActivity : AppCompatActivity() {
    var appDatabase: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feeding)

        appDatabase = AppDatabase.getInstance(this)
        this.setDataEventListener()
        this.setDateTimeListener()
        this.setCurrentDate()
        this.setCurrentTime()
    }

    private fun setDataEventListener() {
        btnInsert.setOnClickListener {
            if(editBrand.text.trim().isEmpty()) {
                Toast.makeText(this@FeedingActivity, "브랜드을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(editAmount.text.trim().isEmpty()) {
                Toast.makeText(this@FeedingActivity, "급여량을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
//                appDatabase?.insulinDao()?.insert(
//                    Insulin(
//                        uid = 0,
//                        date = editTextDate.text.toString().replace("-",""),
//                        time = editTextTime.text.toString().replace(":",""),
//                        type = if(rdoDried.isChecked) 0 else 1,
//                        undiluted = editUndiluted.text.toString().toFloat(),
//                        totalCapacity = editTotalCapacity.text.toString().toFloat(),
//                        dilution = if(chkDilution.isChecked) 1 else 0,
//                        remark = editRemark.text.toString()
//                    )
//                )
            }
        }

//        btnList.setOnClickListener {
//            CoroutineScope(Dispatchers.IO).launch {
//                val cal = Calendar.getInstance()
//                val myFormat = "yyyyMMdd" // mention the format you need
//                val sdf = SimpleDateFormat(myFormat, Locale.US)
//
////                val insulins = appDatabase?.insulinDao()?.findByToday(sdf.format(cal.time).toString()) ?: return@launch
////                for (ins: Insulin in insulins) {
////                    println("${ins.uid} => date : ${ins.date.toString()}")
////                    println("${ins.uid} => time : ${ins.time.toString()}")
////                    println("${ins.uid} => type : ${if(ins.type == 0) "휴물린엔" else "캐닌슐린"}")
////                    println("${ins.uid} => undt : ${ins.undiluted.toString()}")
////                    println("${ins.uid} => dilt : ${if(ins.dilution == 1) "희석" else "희석X"}")
////                    println("${ins.uid} => volm : ${ins.totalCapacity.toString()}")
////                    println("${ins.uid} => remk : ${ins.remark.toString()}")
////                    println("============================")
////                    println("============================")
////                }
//            }
//        }

//        btnDeleteAll.setOnClickListener {
//            CoroutineScope(Dispatchers.IO).launch {
//                appDatabase?.insulinDao()?.deleteAll()
//            }
//        }
    }

    private fun setCurrentDate() {
        val cal = Calendar.getInstance()
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editTextDate.setText(sdf.format(cal.time))
    }

    private fun setCurrentTime() {
        val cal = Calendar.getInstance()
        val myFormat = "HH:mm" // mention the format you needa
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editTextTime.setText(sdf.format(cal.time))
    }

    private fun setDateTimeListener() {
        editTextDate.setOnTouchListener { _: View, event: MotionEvent ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
//                    Toast.makeText(this@MainActivity, "focused", Toast.LENGTH_SHORT).show()
                    val cal = Calendar.getInstance()
                    val y = cal.get(Calendar.YEAR)
                    val m = cal.get(Calendar.MONTH)
                    val d = cal.get(Calendar.DAY_OF_MONTH)
                    val datepickerdialog: DatePickerDialog = DatePickerDialog(this@FeedingActivity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val myFormat = "yyyy-MM-dd" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        cal.set(year, monthOfYear, dayOfMonth)
                        editTextDate!!.setText(sdf.format(cal.time))
                    }, y, m, d)

                    datepickerdialog.show()
                    false
                }
            }

            true
        }

        editTextTime.setOnTouchListener { _: View, event: MotionEvent ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
//                    Toast.makeText(this@MainActivity, "focused", Toast.LENGTH_SHORT).show()
                    val c:Calendar= Calendar.getInstance()
                    val hh=c.get(Calendar.HOUR_OF_DAY)
                    val mm=c.get(Calendar.MINUTE)
                    val timePickerDialog: TimePickerDialog = TimePickerDialog(this@FeedingActivity,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                            editTextTime!!.setText( ""+hourOfDay + ":" + minute);
                        },hh,mm,true)
                    timePickerDialog.show()
                    false
                }
            }
            true
        }

//        editTextDate.setOnFocusChangeListener { view, hasFocus ->
//            if(hasFocus)
//                Toast.makeText(this@MainActivity, "focused", Toast.LENGTH_SHORT).show()
//            else
//                Toast.makeText(this@MainActivity, "focuse lose", Toast.LENGTH_SHORT).show()
//
//        }
    }

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        super.onDestroy()
    }
}