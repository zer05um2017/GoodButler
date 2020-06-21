package com.j2d2.bloodglucose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.j2d2.R
import com.j2d2.insulin.Insulin
import com.j2d2.main.AppDatabase
import kotlinx.android.synthetic.main.activity_blood_glucose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class BloodGlucoseActivity : AppCompatActivity() {
    private var appDatabase: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blood_glucose)

        appDatabase = AppDatabase.getInstance(this)
        setDataEventListener()
        setDateTimeListener()
        setCurrentDate()
        setCurrentTime()
        //loadData()
    }

    private fun getBloodGlucose():Int {
        return editValue.text.toString().toInt()
    }

    private fun setDataEventListener() {
        btnInput.setOnClickListener {
            if (editValue.text.trim().isEmpty()) {
                Toast.makeText(this@BloodGlucoseActivity, getString(R.string.com_j2d2_bloodglucose_blood_message_input_value), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                appDatabase?.bloodGlucoseDao()?.insert(
                    BloodGlucose(
                        uid = 0,
                        date = getCurrentDate(),
                        time = getCurrentTime(),
                        bloodSugar = getBloodGlucose()
                    )
                )

                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(
                        this@BloodGlucoseActivity,
                        getString(R.string.com_j2d2_bloodglucose_blood_message_input_complete),
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }
    }

    /**
     * 현재 날짜값 설정
     * @since 2020.06.18
     * @author perry912
     * @return date
     */
    private fun setCurrentDate() {
        val cal = Calendar.getInstance()
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editTextDate.setText(sdf.format(cal.time))
    }

    /**
     * 현재 시간값 설정
     * @since 2020.06.18
     * @author perry912
     * @return date
     */
    private fun setCurrentTime() {
        val cal = Calendar.getInstance()
        val myFormat = "HH:mm" // mention the format you needa
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editTextTime.setText(sdf.format(cal.time))
    }

    /**
     * 사료 급여 날짜
     * @since 2020.06.18
     * @author perry912
     * @return date
     */
    private fun getCurrentDate(): String {
        return editTextDate.text.toString().replace("-", "")
    }

    /**
     * 사료 급여 시간
     * @since 2020.06.18
     * @author perry912
     * @return time
     */
    private fun getCurrentTime(): String {
        return editTextTime.text.toString().replace(":", "")
    }

    private fun setDateTimeListener() {
        editTextDate.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val cal = Calendar.getInstance()
                    val y = cal.get(Calendar.YEAR)
                    val m = cal.get(Calendar.MONTH)
                    val d = cal.get(Calendar.DAY_OF_MONTH)
                    val datepickerdialog: DatePickerDialog = DatePickerDialog(
                        this@BloodGlucoseActivity,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            val myFormat = "yyyy-MM-dd" // mention the format you need
                            val sdf = SimpleDateFormat(myFormat, Locale.US)
                            cal.set(year, monthOfYear, dayOfMonth)
                            editTextDate!!.setText(sdf.format(cal.time))
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

        editTextTime.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val c: Calendar = Calendar.getInstance()
                    val hh = c.get(Calendar.HOUR_OF_DAY)
                    val mm = c.get(Calendar.MINUTE)
                    val timePickerDialog: TimePickerDialog = TimePickerDialog(
                        this@BloodGlucoseActivity,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                            editTextTime!!.setText("" + hourOfDay + ":" + minute);
                        }, hh, mm, true
                    )
                    timePickerDialog.show()
                    false
                }
            }
            true
        }
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val cal = Calendar.getInstance()
            val myFormat = "yyyyMMdd" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val glucose =
                appDatabase?.bloodGlucoseDao()?.findByToday(sdf.format(cal.time).toString())
                    ?: return@launch
            for (gcs: BloodGlucose in glucose) {
                println("${gcs.uid} => date : ${gcs.date.toString()}")
                println("${gcs.uid} => time : ${gcs.time.toString()}")
                println("${gcs.uid} => type : ${gcs.bloodSugar.toString()}")
            }
        }
    }
}