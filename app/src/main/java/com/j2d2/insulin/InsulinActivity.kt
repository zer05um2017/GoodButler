package com.j2d2.insulin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.j2d2.R
import com.j2d2.main.AppDatabase
import com.j2d2.main.SharedPref
import kotlinx.android.synthetic.main.activity_insulin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class InsulinActivity : AppCompatActivity() {
    var appDatabase: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_insulin)
        setContentView(R.layout.activity_insulin)

        appDatabase = AppDatabase.getInstance(this)
        setDataEventListener()
        setDateTimeListener()
        setCurrentDate()
        setCurrentTime()
    }

    override fun onStart() {
        super.onStart()
        getLatestInputDataFromPreference()
    }

    /**
     * 현재 날짜값 설정
     */
    private fun setCurrentDate() {
        val cal = Calendar.getInstance()
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editTextDate.setText(sdf.format(cal.time))
    }

    /**
     * 현재 시간값 설정
     */
    private fun setCurrentTime() {
        val cal = Calendar.getInstance()
        val myFormat = "HH:mm" // mention the format you needa
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editTextTime.setText(sdf.format(cal.time))
    }

    /**
     * 인슐린 주사 날짜
     * @since 2020.06.17
     * @author perry912
     * @return date
     */
    private fun getCurrentDate(): String {
        return editTextDate.text.toString().replace("-", "")
    }

    /**
     * 인슐린 주사 시간
     * @since 2020.06.17
     * @author perry912
     * @return time
     */
    private fun getCurrentTime(): String {
        return editTextTime.text.toString().replace(":", "")
    }

    /**
     * 인슐린 타입
     * @since 2020.06.17
     * @author perry912
     * @return 0:휴물린엔, 1:캐닌슐린
     */
    private fun getInsulinType(): Int {
        return if (rdoHumulinN.isChecked) 0 else 1
    }

    /**
     * 인슐린 원액량
     * @since 2020.06.17
     * @author perry912
     * @return float type
     */
    private fun getUndilutedCapacity(): Float {
        return editUndiluted.text.toString().toFloat()
    }

    /**
     * 주사량
     * @since 2020.06.17
     * @author perry912
     * @return int type
     */
    private fun getTotalInjectionCapacity(): Int {
        return editTotalCapacity.text.toString().toInt()
    }

    /**
     * 희석여부
     * @since 2020.06.17
     * @author perry912
     * @return 1:희석, 0:희석 안함
     */
    private fun isDiluted(): Int {
        return if (chkDilution.isChecked) 1 else 0
    }

    private fun getMemo(): String {
        return editRemark.text.toString()
    }

    private fun setDataEventListener() {
        btnSave.setOnClickListener {
            if (editUndiluted.text.trim().isEmpty()) {
                Toast.makeText(
                    this@InsulinActivity,
                    getString(R.string.com_j2d2_insulin_ins_message_undiluted_capacity),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (editTotalCapacity.text.trim().isEmpty()) {
                Toast.makeText(
                    this@InsulinActivity,
                    getString(R.string.com_j2d2_insulin_ins_message_total_insulin_capacity),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                appDatabase?.insulinDao()?.insert(
                    Insulin(
                        uid = 0,
                        date = getCurrentDate(),
                        time = getCurrentTime(),
                        type = getInsulinType(),
                        undiluted = getUndilutedCapacity(),
                        totalCapacity = getTotalInjectionCapacity(),
                        dilution = isDiluted(),
                        remark = getMemo()
                    )
                )

                GlobalScope.launch(Dispatchers.Main) {
                    setLatestInputDataIntoPreference()
                    Toast.makeText(
                        this@InsulinActivity,
                        getString(R.string.com_j2d2_insulin_ins_message_input_complete),
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
        }

        btnList.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val cal = Calendar.getInstance()
                val myFormat = "yyyyMMdd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
//                cal.set(year, monthOfYear, dayOfMonth)
//                sdf.format(cal.time)
                val insulins =
                    appDatabase?.insulinDao()?.findByToday(sdf.format(cal.time).toString())
                        ?: return@launch
                for (ins: Insulin in insulins) {
                    println("${ins.uid} => date : ${ins.date.toString()}")
                    println("${ins.uid} => time : ${ins.time.toString()}")
                    println("${ins.uid} => type : ${if (ins.type == 0) "휴물린엔" else "캐닌슐린"}")
                    println("${ins.uid} => undt : ${ins.undiluted.toString()}")
                    println("${ins.uid} => dilt : ${if (ins.dilution == 1) "희석" else "희석X"}")
                    println("${ins.uid} => volm : ${ins.totalCapacity.toString()}")
                    println("${ins.uid} => remk : ${ins.remark.toString()}")
                }
            }
        }

        btnDeleteAll.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                appDatabase?.insulinDao()?.deleteAll()
            }
        }
    }

    /**
     * @author perry912
     * @since 2020.06.17
     * 현재 입력 값 암호화해서 Preference에 저장
     */
    private fun setLatestInputDataIntoPreference() {
        with(SharedPref.prefs.edit()) {
            putInt(R.string.com_j2d2_insulin_ins_type.toString(), getInsulinType())
            putFloat(
                R.string.com_j2d2_insulin_ins_undiluted_capacity.toString(),
                getUndilutedCapacity()
            )
            putInt(
                R.string.com_j2d2_insulin_ins_total_capacity.toString(),
                getTotalInjectionCapacity()
            )
            putInt(R.string.com_j2d2_insulin_ins_dilution.toString(), isDiluted())
            putString(R.string.com_j2d2_insulin_ins_memo.toString(), getMemo())
            commit()
        }
    }

    /**
     * @author perry912
     * @since 2020.06.17
     * 입력된 Preference 가져오기
     */
    private fun getLatestInputDataFromPreference() {
        with(SharedPref.prefs) {
            if(contains(R.string.com_j2d2_insulin_ins_type.toString())) {
                if (getInt(R.string.com_j2d2_insulin_ins_type.toString(), 0) == 0) {
                    rdoHumulinN!!.post {
                        rdoHumulinN!!.isChecked = true
                        rdoHumulinN!!.jumpDrawablesToCurrentState()
                    }
                } else {
                    rdoCaninsulin!!.post {
                        rdoCaninsulin!!.isChecked = true
                        rdoCaninsulin!!.jumpDrawablesToCurrentState()
                    }
                }
            }

            if(contains(R.string.com_j2d2_insulin_ins_dilution.toString())) {
                if (getInt(R.string.com_j2d2_insulin_ins_dilution.toString(), 0) == 1) {
                    chkDilution!!.post {
                        chkDilution!!.isChecked = true
                        chkDilution!!.jumpDrawablesToCurrentState()
                    }
                }
            }

            if(contains(R.string.com_j2d2_insulin_ins_undiluted_capacity.toString())) {
                editUndiluted.setText(
                    getFloat(
                        R.string.com_j2d2_insulin_ins_undiluted_capacity.toString(),
                        0.0F
                    ).toString()
                )
            }

            if(contains(R.string.com_j2d2_insulin_ins_total_capacity.toString())) {
                editTotalCapacity.setText(
                    getInt(
                        R.string.com_j2d2_insulin_ins_total_capacity.toString(),
                        0
                    ).toString()
                )
                editTotalCapacity.requestFocus()
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            }

            if(contains(R.string.com_j2d2_insulin_ins_memo.toString())) {
                editRemark.setText(getString(R.string.com_j2d2_insulin_ins_memo.toString(), ""))
            }
        }
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
                        this@InsulinActivity,
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
                        this@InsulinActivity,
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

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        super.onDestroy()
    }
}