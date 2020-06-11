package com.j2d2.insulin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.j2d2.R
import kotlinx.android.synthetic.main.activity_insulin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class InsulinActivity : AppCompatActivity() {
    var appDatabase: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insulin)

        appDatabase = AppDatabase.getInstance(this)
        this.setDataEventListener()
        this.setDateTimeListener()
        this.setCurrentDate()
        this.setCurrentTime()
    }

    private fun setCurrentDate() {
        val cal = Calendar.getInstance()
        val myFormat = "M-dd-yyyy" // mention the format you needa
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editTextDate.setText(sdf.format(cal.time))
    }

    private fun setCurrentTime() {
        val cal = Calendar.getInstance()
        val myFormat = "HH:mm" // mention the format you needa
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editTextTime.setText(sdf.format(cal.time))
    }

    private fun setDataEventListener() {
        btnInsert.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                appDatabase?.userDao()?.insert(User(0, "엥", "휴"))
            }
        }

        btnList.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val user = appDatabase?.userDao()?.getAll()
                for (u:User in user!!) {
                    println("${u.uid} => First Name : ${u.firstName.toString()} Last Name : ${u.lastName.toString()}")
                }
            }
        }

        btnDeleteAll.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                appDatabase?.userDao()?.deleteAll()
            }
        }
    }

    private fun setDateTimeListener() {
        editTextDate.setOnTouchListener { _: View, event:MotionEvent ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
//                    Toast.makeText(this@MainActivity, "focused", Toast.LENGTH_SHORT).show()
                    val cal = Calendar.getInstance()
                    val y = cal.get(Calendar.YEAR)
                    val m = cal.get(Calendar.MONTH)
                    val d = cal.get(Calendar.DAY_OF_MONTH)
                    val datepickerdialog:DatePickerDialog = DatePickerDialog(this@InsulinActivity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        val myFormat = "MM/dd/yyyy" // mention the format you needa
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

        editTextTime.setOnTouchListener { _: View, event:MotionEvent ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
//                    Toast.makeText(this@MainActivity, "focused", Toast.LENGTH_SHORT).show()
                    val c:Calendar= Calendar.getInstance()
                    val hh=c.get(Calendar.HOUR_OF_DAY)
                    val mm=c.get(Calendar.MINUTE)
                    val timePickerDialog:TimePickerDialog=TimePickerDialog(this@InsulinActivity,
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