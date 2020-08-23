package com.j2d2.insulin

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.j2d2.R
import com.j2d2.main.database.AppDatabase
import com.j2d2.main.MainApp
import com.j2d2.main.SharedPref
import kotlinx.android.synthetic.main.activity_insulin.*
import kotlinx.android.synthetic.main.activity_insulin.btnSave
import kotlinx.android.synthetic.main.activity_insulin.editRemark
import kotlinx.android.synthetic.main.activity_insulin.editTextDate
import kotlinx.android.synthetic.main.activity_insulin.editTextTime
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class InsulinActivity : AppCompatActivity() {
    var appDatabase: AppDatabase? = null
    private var isModifyed:Boolean? = false
    private lateinit var parcelData:InsulinParcel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_insulin_title)
        setContentView(R.layout.activity_insulin)

        appDatabase = AppDatabase.getInstance(this)
        setDataEventListener()
        setDateTimeListener()

        insulinType.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                when(position) {
//                    0 -> {
//
//                    }
//                    1 -> {
//
//                    }
//                    2 -> {
//
//                    }
//                    3 -> {
//
//                    }
//                    4 -> {
//
//                    }
//                    5 -> {
//
//                    }
//                    6 -> {
//
//                    }
//                    7 -> {
//
//                    }
//                    8 -> {
//
//                    }
//                    9 -> {
//
//                    }
//                    10 -> {
//
//                    }
//                }
//                when (insulinType.getItemAtPosition(position)) {
//                    "Android Dev Summit" -> {
//                        updateAndroidSubmit()
//                    }
//                    "Android I/O" -> {
//                        updateGoogleIO()
//                    }
//                    else -> {
//                        updateAndroidSubmit()
//                    }
//                }
            }
        }

        if (intent.hasExtra("data")) {
            isModifyed = true
            var data = intent.getParcelableExtra<InsulinParcel>("data")
            if (data != null) {
                parcelData = data
            }
            if (data != null) {
                insulinType.setSelection(data.type)
            }
            editUndiluted?.setText(data?.undiluted.toString())

            if (data != null) {
                when(data.dilution) {
                    0 -> {chkDilution.isChecked = false}
                    1 -> {chkDilution.isChecked = true}
                }
            }
            editTotalCapacity?.setText(data?.totalCapacity.toString())
            editRemark?.setText(data?.remark)

            val calendar = GregorianCalendar.getInstance()
            if (data != null) {
                calendar.timeInMillis = data.millis
            }
            editTextDate.setText("%02d-%02d-%02d".format(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)))
            editTextTime.setText("%02d:%02d".format(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)))
        } else {
            setCurrentDate()
            setCurrentTime()
        }
    }

    override fun onStart() {
        super.onStart()
        if(!isModifyed!!) {
            getLatestInputDataFromPreference()
        }
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
     * 시
     * @since 2020.06.17
     * @author perry912
     * @return milliseconds:Long
     */
    private fun getTimeInMillis(): Long {
        val date = editTextDate.text.split("-")
        val time = editTextTime.text.toString().split(":")

        return GregorianCalendar(date[0].toInt(), date[1].toInt(), date[2].toInt(), time[0].toInt(), time[1].toInt()).timeInMillis
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
     * @return
        휴물린		0
        캐닌슐린		1
        노보린		2
        란투스		3
        이노렛		4
        애피드라		5
        노보래피드	    6
        레버미어		7
        휴마로그		8
        휴마로그믹스	9
        노보믹스		10
     */
    private fun getInsulinType(): Int {
        return insulinType.selectedItemPosition
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

            if (editTotalCapacity.text.trim().toString().contains(".")) {
                Toast.makeText(
                    this@InsulinActivity,
                    getString(R.string.com_j2d2_insulin_ins_message_total_insulin_type_wrong),
                    Toast.LENGTH_SHORT
                ).show()
                editTotalCapacity.requestFocus()
                return@setOnClickListener
            }

            if (editTotalCapacity.text.trim().isEmpty()) {
                Toast.makeText(
                    this@InsulinActivity,
                    getString(R.string.com_j2d2_insulin_ins_message_total_insulin_capacity),
                    Toast.LENGTH_SHORT
                ).show()
                editTotalCapacity.requestFocus()
                return@setOnClickListener
            }
            val modified = this.isModifyed
            CoroutineScope(Dispatchers.IO).launch {
                if(modified!!) {
                    val data = Insulin(
                        parcelData.millis,
                        parcelData.dataType,
                        parcelData.type,
                        parcelData.undiluted,
                        parcelData.totalCapacity,
                        parcelData.dilution,
                        parcelData.remark,
                        parcelData.petId
                    )
                    appDatabase?.insulinDao()?.delete(data)
                }
                appDatabase?.insulinDao()?.insert(
                    Insulin(
                        millis = getTimeInMillis(),
                        dataType = 1,   // 데이터 타입 0:사료, 1:인슐린
                        type = getInsulinType(),
                        undiluted = getUndilutedCapacity(),
                        totalCapacity = getTotalInjectionCapacity(),
                        dilution = isDiluted(),
                        remark = getMemo(),
                        petId = MainApp.getSelectedPetId()
                    )
                )

                GlobalScope.launch(Dispatchers.Main) {
                    setLatestInputDataIntoPreference()
                    Toast.makeText(
                        this@InsulinActivity,
                        getString(R.string.com_j2d2_insulin_ins_message_input_complete),
                        Toast.LENGTH_LONG
                    ).show()
                    if(modified!!) {
                        setResult(Activity.RESULT_OK)
                    }
                    finish()
                }
            }
        }

        btnList.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val cal = Calendar.getInstance()
                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
//                cal.set(year, monthOfYear, dayOfMonth)
//                sdf.format(cal.time)
                val insulins =
                    appDatabase?.insulinDao()?.findByToday(sdf.format(cal.time).toString())
                        ?: return@launch
                for (ins: Insulin in insulins) {
                    println("date : ${ins.millis.toString()}")
                    println("type : ${when (ins.type) {
                        0 -> {
                            "휴물린엔"
                        }
                        1 -> {
                            "캐닌슐린"
                        }
                        2 -> {
                            "노보린"
                        }
                        3 -> {
                            "란투스"
                        }
                        4 -> {
                            "이노렛"
                        }
                        5 -> {
                            "애피드라"
                        }
                        6 -> {
                            "노보래피드"
                        }
                        7 -> {
                            "레버미어"
                        }
                        8 -> {
                            "휴마로그"
                        }
                        9 -> {
                            "휴마로그믹스"
                        }
                        10 -> {
                            "노보믹스"
                        }
                        else -> {
                            "기타"
                        }
                    }}")
                    println("undt : ${ins.undiluted.toString()}")
                    println("dilt : ${if (ins.dilution == 1) "희석" else "희석X"}")
                    println("volm : ${ins.totalCapacity.toString()}")
                    println("remk : ${ins.remark.toString()}")
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
                insulinType.setSelection(getInt(R.string.com_j2d2_insulin_ins_type.toString(), 0))
//                if (getInt(R.string.com_j2d2_insulin_ins_type.toString(), 0) == 0) {
//                    rdoHumulinN!!.post {
//                        rdoHumulinN!!.isChecked = true
//                        rdoHumulinN!!.jumpDrawablesToCurrentState()
//                    }
//                } else {
//                    rdoCaninsulin!!.post {
//                        rdoCaninsulin!!.isChecked = true
//                        rdoCaninsulin!!.jumpDrawablesToCurrentState()
//                    }
//                }
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
                    var y:Int
                    var m:Int
                    var d:Int
                    val cal = GregorianCalendar.getInstance()
                    if(this!!.isModifyed!!) {
                        cal.timeInMillis = parcelData.millis
                        y = cal.get(Calendar.YEAR)
                        m = cal.get(Calendar.MONTH) - 1
                        d = cal.get(Calendar.DAY_OF_MONTH)
                    } else {
                        y = cal.get(Calendar.YEAR)
                        m = cal.get(Calendar.MONTH)
                        d = cal.get(Calendar.DAY_OF_MONTH)
                    }

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
                    var hh:Int
                    var mm:Int
                    val cal = GregorianCalendar.getInstance()

                    if(this!!.isModifyed!!) {
                        cal.timeInMillis = parcelData.millis
                        hh = cal.get(Calendar.HOUR_OF_DAY)
                        mm = cal.get(Calendar.MINUTE)
                    } else {
                        hh = cal.get(Calendar.HOUR_OF_DAY)
                        mm = cal.get(Calendar.MINUTE)
                    }
                    val timePickerDialog: TimePickerDialog = TimePickerDialog(
                        this@InsulinActivity,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                            val sdf = SimpleDateFormat("HH:mm")
                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            cal.set(Calendar.MINUTE, minute)
                            editTextTime!!.setText(sdf.format(cal.time))
                        }, hh, mm, false
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

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}