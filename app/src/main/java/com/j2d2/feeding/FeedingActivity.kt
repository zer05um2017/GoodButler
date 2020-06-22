package com.j2d2.feeding

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
import kotlinx.android.synthetic.main.activity_blood_glucose.*
import kotlinx.android.synthetic.main.activity_feeding.*
import kotlinx.android.synthetic.main.activity_feeding.btnSave
import kotlinx.android.synthetic.main.activity_feeding.editRemark
import kotlinx.android.synthetic.main.activity_feeding.editTextDate
import kotlinx.android.synthetic.main.activity_feeding.editTextTime
import kotlinx.android.synthetic.main.activity_insulin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FeedingActivity : AppCompatActivity() {
    private var appDatabase: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_feeding)
        setContentView(R.layout.activity_feeding)

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

    private fun setDataEventListener() {
        btnSave.setOnClickListener {
            if (editBrand.text.trim().isEmpty()) {
                Toast.makeText(this@FeedingActivity, getString(R.string.com_j2d2_feeding_feed_message_brand), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editAmount.text.trim().isEmpty()) {
                Toast.makeText(this@FeedingActivity, getString(R.string.com_j2d2_feeding_feed_message_amount), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                appDatabase?.feedingDao()?.insert(
                    Feeding(
                        uid = 0,
                        date = getCurrentDate(),
                        time = getCurrentTime(),
                        type = isDriedMethod(),
                        brandName = getBrandName(),
                        feedingAmount = getFeedingAmount(),
                        remark = getMemo()
                    )
                )

                GlobalScope.launch(Dispatchers.Main) {
                    setLatestInputDataIntoPreference()
                    Toast.makeText(
                        this@FeedingActivity,
                        getString(R.string.com_j2d2_feeding_feed_message_input_complete),
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
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

    /**
     * 사료 타입
     * @since 2020.06.18
     * @author perry912
     * @return 0:건식, 1:습식
     */
    private fun getFeedingType(): Int {
        return if (rdoDryMethod.isChecked) 0 else 1
    }

    /**
     * 사료 브랜드
     * @since 2020.06.17
     * @author perry912
     * @return float type
     */
    private fun getBrandName(): String {
        return editBrand.text.toString()
    }

    /**
     * 급여량
     * @since 2020.06.17
     * @author perry912
     * @return int type
     */
    private fun getFeedingAmount(): Int {
        return editAmount.text.toString().toInt()
    }
    /**
     * 메
     * @since 2020.06.21
     * @author perry912
     * @return String type
     */
    private fun getMemo(): String {
        return editRemark.text.toString()
    }

    /**
     * 사료종류
     * @since 2020.06.21
     * @author perry912
     * @return 0:건식, 1:습식
     */
    private fun isDriedMethod(): Int {
        return if(rdoDryMethod.isChecked) 0 else 1
    }

    /**
     * @author perry912
     * @since 2020.06.17
     * 현재 입력 값 암호화해서 Preference에 저장
     */
    private fun setLatestInputDataIntoPreference() {
        with(SharedPref.prefs.edit()) {
            putInt(R.string.com_j2d2_feeding_feed_type.toString(), getFeedingType())
            putString(
                R.string.com_j2d2_feeding_feed_brand_name.toString(),
                getBrandName()
            )
            putInt(
                R.string.com_j2d2_feeding_feed_amount.toString(),
                getFeedingAmount()
            )
            putString(R.string.com_j2d2_feeding_feed_memo.toString(), getMemo())
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
            if(contains(R.string.com_j2d2_feeding_feed_type.toString())) {
                if (getInt(R.string.com_j2d2_feeding_feed_type.toString(), 0) == 0) {
                    rdoDryMethod!!.post {
                        rdoDryMethod!!.isChecked = true
                        rdoDryMethod!!.jumpDrawablesToCurrentState()
                    }
                } else {
                    rdoWetMethod!!.post {
                        rdoWetMethod!!.isChecked = true
                        rdoWetMethod!!.jumpDrawablesToCurrentState()
                    }
                }
            }

            if(contains(R.string.com_j2d2_feeding_feed_brand_name.toString())) {
                editBrand.setText(
                    getString(
                        R.string.com_j2d2_feeding_feed_brand_name.toString(),
                        ""
                    ).toString()
                )
                editAmount.requestFocus()
            } else {
                editBrand.requestFocus()
            }

            if(contains(R.string.com_j2d2_feeding_feed_amount.toString())) {
                editAmount.setText(
                    getInt(
                        R.string.com_j2d2_feeding_feed_amount.toString(),
                        0
                    ).toString()
                )
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            }

            if(contains(R.string.com_j2d2_feeding_feed_memo.toString())) {
                editRemark.setText(getString(R.string.com_j2d2_feeding_feed_memo.toString(), ""))
            }
        }
    }

    private fun setDateTimeListener() {
        editTextDate.setOnTouchListener { _: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
//                    Toast.makeText(this@MainActivity, "focused", Toast.LENGTH_SHORT).show()
                    val cal = Calendar.getInstance()
                    val y = cal.get(Calendar.YEAR)
                    val m = cal.get(Calendar.MONTH)
                    val d = cal.get(Calendar.DAY_OF_MONTH)
                    val datepickerdialog: DatePickerDialog = DatePickerDialog(
                        this@FeedingActivity,
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
                        this@FeedingActivity,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                            val sdf = SimpleDateFormat("HH:mm")
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            c.set(Calendar.MINUTE, minute)
                            editTextTime!!.setText(sdf.format(c.time))
                        }, hh, mm, false
                    )
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