package com.j2d2.insulin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.j2d2.R
import kotlinx.android.synthetic.main.activity_insulin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class InsulinActivity : AppCompatActivity() {
    var appDatabase: AppDatabase? = null
    val PREPERANCE_FILE: String = "EncrypedInfo"

    companion object {
        fun isOverMashmellow(): Boolean {
            if(Build.VERSION <= 23) {
                return false
            }
            return true
        }
    }

    private val sharedEnPreferences: SharedPreferences by lazy {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        EncryptedSharedPreferences.create(
            PREPERANCE_FILE,
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }

    private val sharedPreferences: SharedPreferences by lazy {
        this@InsulinActivity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insulin)

        appDatabase = AppDatabase.getInstance(this)
        setDataEventListener()
        setDateTimeListener()
        setCurrentDate()
        setCurrentTime()
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
        return editTextDate.text.toString().replace("-","")
    }

    /**
     * 인슐린 주사 시간
     * @since 2020.06.17
     * @author perry912
     * @return time
     */
    private fun getCurrentTime(): String {
        return editTextTime.text.toString().replace(":","")
    }

    /**
     * 인슐린 타입
     * @since 2020.06.17
     * @author perry912
     * @return 0:휴물린엔, 1:캐닌슐린
     */
    private fun getInsulinType(): Int {
        return if(rdoHumulinN.isChecked) 0 else 1
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
     * @return float type
     */
    private fun getTotalInjectionCapacity(): Float {
        return editTotalCapacity.text.toString().toFloat()
    }

    /**
     * 희석여부
     * @since 2020.06.17
     * @author perry912
     * @return 1:희석, 0:희석 안함
     */
    private fun isDiluted(): Int {
        return if(chkDilution.isChecked) 1 else 0
    }

    private fun getMemo(): String {
        return editRemark.text.toString()
    }

    private fun setDataEventListener() {
        btnInsert.setOnClickListener {
            if(editUndiluted.text.trim().isEmpty()) {
                Toast.makeText(this@InsulinActivity, getString(R.string.com_j2de_insulin_ins_message_undiluted_capacity), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(editTotalCapacity.text.trim().isEmpty()) {
                Toast.makeText(this@InsulinActivity, getString(R.string.com_j2de_insulin_ins_message_total_insulin_capacity), Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@InsulinActivity, getString(R.string.com_j2de_insulin_ins_message_input_complete), Toast.LENGTH_LONG).show()
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
                val insulins = appDatabase?.insulinDao()?.findByToday(sdf.format(cal.time).toString()) ?: return@launch
                for (ins:Insulin in insulins) {
                    println("${ins.uid} => date : ${ins.date.toString()}")
                    println("${ins.uid} => time : ${ins.time.toString()}")
                    println("${ins.uid} => type : ${if(ins.type == 0) "휴물린엔" else "캐닌슐린"}")
                    println("${ins.uid} => undt : ${ins.undiluted.toString()}")
                    println("${ins.uid} => dilt : ${if(ins.dilution == 1) "희석" else "희석X"}")
                    println("${ins.uid} => volm : ${ins.totalCapacity.toString()}")
                    println("${ins.uid} => remk : ${ins.remark.toString()}")
                    println("============================")
                    println("============================")
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

        try {
            val sharedPrefsEditor = sharedEnPreferences.edit()

            sharedPrefsEditor.putInt(R.string.com_j2d2_insulin_ins_type.toString(), getInsulinType())
            sharedPrefsEditor.putFloat(R.string.com_j2d2_insulin_ins_undiluted_capacity.toString(), getUndilutedCapacity())
            sharedPrefsEditor.putFloat(R.string.com_j2de_insulin_ins_message_total_insulin_capacity.toString(), getTotalInjectionCapacity())
            sharedPrefsEditor.putInt(R.string.com_j2d2_insulin_ins_dilution.toString(), isDiluted())
            sharedPrefsEditor.putString(R.string.com_j2d2_insulin_ins_memo.toString(), getMemo())
        }
    }

    private fun getLatestInputDataFromPreference() {
        val sharedPrefsReader = sharedEnPreferences

        if(sharedPrefsReader.getInt(R.string.com_j2d2_insulin_ins_type.toString(), 0) == 0) {
            rdoHumulinN.callOnClick()
        } else {
            rdoCaninsulin.callOnClick()
        }

        editUndiluted.setText(sharedPrefsReader.getFloat(R.string.com_j2d2_insulin_ins_undiluted_capacity.toString(), 0.0F).toString())
        editTotalCapacity.setText(sharedPrefsReader.getFloat(R.string.com_j2de_insulin_ins_message_total_insulin_capacity.toString(),0.0F).toString())

        if(sharedPrefsReader.getInt(R.string.com_j2d2_insulin_ins_dilution.toString(), 0) == 0) {
            rdoHumulinN.callOnClick()
        }

        editRemark.setText(sharedPrefsReader.getString(R.string.com_j2d2_insulin_ins_memo.toString(), ""))
//        type = if(rdoHumulinN.isChecked) 0 else 1,
//        undiluted = editUndiluted.text.toString().toFloat(),
//        totalCapacity = editTotalCapacity.text.toString().toFloat(),
//        dilution = if(chkDilution.isChecked) 1 else 0,
//        remark = editRemark.text.toString()
    }

    private fun setDateTimeListener() {
        editTextDate.setOnTouchListener { _: View, event:MotionEvent ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val cal = Calendar.getInstance()
                    val y = cal.get(Calendar.YEAR)
                    val m = cal.get(Calendar.MONTH)
                    val d = cal.get(Calendar.DAY_OF_MONTH)
                    val datepickerdialog:DatePickerDialog = DatePickerDialog(this@InsulinActivity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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

        editTextTime.setOnTouchListener { _: View, event:MotionEvent ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
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