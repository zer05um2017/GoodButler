package com.j2d2.feeding

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.j2d2.R
import com.j2d2.main.AppDatabase
import com.j2d2.main.SharedPref
import kotlinx.android.synthetic.main.activity_feeding.*
import kotlinx.android.synthetic.main.activity_feeding.btnSave
import kotlinx.android.synthetic.main.activity_feeding.editRemark
import kotlinx.android.synthetic.main.activity_feeding.editTextDate
import kotlinx.android.synthetic.main.activity_feeding.editTextTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FeedingActivity : AppCompatActivity() {
    private var appDatabase: AppDatabase? = null
    private var isModifyed:Boolean? = false
    private lateinit var pacelData:FeedParcel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_feeding_title)
        setContentView(R.layout.activity_feeding)
        appDatabase = AppDatabase.getInstance(this)
        setDataEventListener()
        setDateTimeListener()

        if (intent.hasExtra("data")) {
            isModifyed = true
            var data = intent.getParcelableExtra<FeedParcel>("data")
            pacelData = data
            editBrand?.setText(data?.brandName)
            editRemark?.setText(data?.remark)
            editAmount?.setText(data?.totalCapacity.toString())
            when(data?.type) {
                0 -> {rdoDryMethod.isChecked = true}
                1 -> {rdoWetMethod.isChecked = true}
            }

            val calendar = GregorianCalendar.getInstance()
            calendar.timeInMillis = data.millis
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

            val modified = this.isModifyed
            CoroutineScope(Dispatchers.IO).launch {
                if(modified!!) {
                    val feed = Feeding(pacelData.millis,
                        pacelData.dataType,
                        pacelData.type,
                        pacelData.brandName,
                        pacelData.totalCapacity,
                        pacelData.remark)
                    appDatabase?.feedingDao()?.delete(feed)
                }
                appDatabase?.feedingDao()?.insert(
                    Feeding(
                        millis = getTimeInMillis(),
                        dataType = 0,   // 데이터 타입 0:사료, 1:인슐린
                        type = isDriedMethod(),
                        brandName = getBrandName(),
                        totalCapacity = getFeedingAmount(),
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
                    if(modified!!) {
                        setResult(Activity.RESULT_OK)
                    }
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

    /**
     * 시간
     * @since 2020.06.18
     * @author perry912
     * @return milliseconds:Long
     */
    private fun getTimeInMillis(): Long {
        val date = editTextDate.text.split("-")
        val time = editTextTime.text.toString().split(":")
        return GregorianCalendar(date[0].toInt(), date[1].toInt(), date[2].toInt(), time[0].toInt(), time[1].toInt()).timeInMillis
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
                    var y:Int
                    var m:Int
                    var d:Int
                    val cal = GregorianCalendar.getInstance()
                    if(this!!.isModifyed!!) {
                        cal.timeInMillis = pacelData.millis
                        y = cal.get(Calendar.YEAR)
                        m = cal.get(Calendar.MONTH)
                        d = cal.get(Calendar.DAY_OF_MONTH)
                    } else {
                        y = cal.get(Calendar.YEAR)
                        m = cal.get(Calendar.MONTH)
                        d = cal.get(Calendar.DAY_OF_MONTH)
                    }

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
                    var hh:Int
                    var mm:Int
                    val cal = GregorianCalendar.getInstance()

                    if(this!!.isModifyed!!) {
                        cal.timeInMillis = pacelData.millis
                        hh = cal.get(Calendar.HOUR_OF_DAY)
                        mm = cal.get(Calendar.MINUTE)
                    } else {
                        hh = cal.get(Calendar.HOUR_OF_DAY)
                        mm = cal.get(Calendar.MINUTE)
                    }

                    val timePickerDialog: TimePickerDialog = TimePickerDialog(
                        this@FeedingActivity,
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