package com.j2d2.bloodglucose

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.j2d2.R
import com.j2d2.feeding.Feeding
import com.j2d2.insulin.Insulin
import com.j2d2.main.AppDatabase
import com.j2d2.main.MainApp
import kotlinx.android.synthetic.main.activity_blood_glucose.*
import kotlinx.android.synthetic.main.activity_blood_glucose.editTextDate
import kotlinx.android.synthetic.main.activity_blood_glucose.editTextTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class BloodGlucoseActivity : AppCompatActivity() {
    private var appDatabase: AppDatabase? = null
    private var isModifyed:Boolean? = false
    private lateinit var parcelData:BloodGlucoseParcel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_bloodglucose_title)
        setContentView(R.layout.activity_blood_glucose)

        appDatabase = AppDatabase.getInstance(this)
        setDataEventListener()
        setDateTimeListener()

        if (intent.hasExtra("data")) {
            isModifyed = true
            var data = intent.getParcelableExtra<BloodGlucoseParcel>("data")
            parcelData = data
            editValue?.setText(data?.bloodSugar.toString())

            val calendar = GregorianCalendar.getInstance()
            calendar.timeInMillis = data.millis
            editTextDate.setText("%02d-%02d-%02d".format(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)))
            editTextTime.setText("%02d:%02d".format(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)))
        } else {
            setCurrentDate()
            setCurrentTime()
        }

//        loadData()
    }

    private fun getBloodGlucose():Int {
        return editValue.text.toString().toInt()
    }

    private fun setDataEventListener() {
        btnGenData.setOnClickListener {
            val dateT = arrayListOf<Long>()
            val bloodT = arrayListOf<Int>()

            dateT.add(1595451600000)
            dateT.add(1595462400000)
            dateT.add(1595469600000)
            dateT.add(1595476800000)
            dateT.add(1595484000000)
            dateT.add(1595491200000)
            dateT.add(1595495220000)
            dateT.add(1595502000000)
            dateT.add(1595509620000)
            dateT.add(1595513220000)
            dateT.add(1595538120000)
            dateT.add(1595545380000)
            dateT.add(1595552760000)
            dateT.add(1595559780000)
            dateT.add(1595567400000)
            dateT.add(1595574000000)
            dateT.add(1595581440000)
            dateT.add(1595588640000)
            dateT.add(1595595840000)
            dateT.add(1595602560000)

            bloodT.add(108)
            bloodT.add(323)
            bloodT.add(290)
            bloodT.add(150)
            bloodT.add(93)
            bloodT.add(81)
            bloodT.add(109)
            bloodT.add(297)
            bloodT.add(257)
            bloodT.add(132)
            bloodT.add(189)
            bloodT.add(297)
            bloodT.add(254)
            bloodT.add(196)
            bloodT.add(150)
            bloodT.add(89)
            bloodT.add(120)
            bloodT.add(323)
            bloodT.add(251)
            bloodT.add(148)

            CoroutineScope(Dispatchers.IO).launch {
                for (i in 0..19) {
                    appDatabase?.bloodGlucoseDao()?.insert(
                        BloodGlucose(
                            millis = dateT[i],
                            dataType = 2,
                            bloodSugar = bloodT[i],
                            petId = MainApp.getSelectedPetId()
                        )
                    )
                }
            }

            val dateInsT = arrayListOf<Long>()
            val amtInsDltT = arrayListOf<Float>()
            val amtInsT = arrayListOf<Int>()

            dateInsT.add(1595452500000)
            dateInsT.add(1595495400000)
            dateInsT.add(1595538900000)
            dateInsT.add(1595582280000)

            amtInsDltT.add(0.4f)
            amtInsDltT.add(0.5f)
            amtInsDltT.add(0.5f)
            amtInsDltT.add(0.4f)

            amtInsT.add(20)
            amtInsT.add(18)
            amtInsT.add(15)
            amtInsT.add(19)

            CoroutineScope(Dispatchers.IO).launch {
                for (i in 0 until 4 step 1) {
                    appDatabase?.insulinDao()?.insert(
                        Insulin(
                            millis = dateInsT[i],
                            dataType = 1,   // 데이터 타입 0:사료, 1:인슐린
                            type = 0,
                            undiluted = amtInsDltT[i],
                            totalCapacity = amtInsT[i],
                            dilution = 1,
                            remark = "정량 주사",
                            petId = MainApp.getSelectedPetId()
                        )
                    )
                }
            }

            val dateFedT = arrayListOf<Long>()
            val amtFedT = arrayListOf<Int>()

            dateFedT.add(1595452200000)
            dateFedT.add(1595495760000)
            dateFedT.add(1595538300000)
            dateFedT.add(1595582220000)

            amtFedT.add(176)
            amtFedT.add(175)
            amtFedT.add(178)
            amtFedT.add(173)

            CoroutineScope(Dispatchers.IO).launch {
                for (i in 0 until 4 step 1) {
                    appDatabase?.feedingDao()?.insert(
                        Feeding(
                            millis = dateFedT[i],
                            dataType = 0,   // 데이터 타입 0:사료, 1:인슐린
                            type = 1,
                            brandName = "W/D",
                            totalCapacity = amtFedT[i],
                            remark = "당근 48g\n오이 49g",
                            petId = MainApp.getSelectedPetId()
                        )
                    )
                }
            }
            Toast.makeText(this@BloodGlucoseActivity, "데이터 생성 완료", Toast.LENGTH_SHORT).show()
        }

        btnInput.setOnClickListener {
            if (editValue.text.trim().isEmpty()) {
                Toast.makeText(this@BloodGlucoseActivity, getString(R.string.com_j2d2_bloodglucose_blood_message_input_value), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val modified = this.isModifyed
            CoroutineScope(Dispatchers.IO).launch {
                if(modified!!) {
                    val bldsugar = BloodGlucose(parcelData.millis,
                        parcelData.dataType,
                        parcelData.bloodSugar,
                        parcelData.petId)
                    appDatabase?.bloodGlucoseDao()?.delete(bldsugar)
                }
                appDatabase?.bloodGlucoseDao()?.insert(
                    BloodGlucose(
                        millis = getTimeInMillis(),
                        dataType = 2,
                        bloodSugar = getBloodGlucose(),
                        petId = MainApp.getSelectedPetId()
                    )
                )

                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(
                        this@BloodGlucoseActivity,
                        getString(R.string.com_j2d2_bloodglucose_blood_message_input_complete),
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
        val myFormat = "HH:mm" // mention the format you need
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
        return editTextTime.text.toString()
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
                        this@BloodGlucoseActivity,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                            val sdf = SimpleDateFormat("HH:mm")
                            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            cal.set(Calendar.MINUTE, minute)
                            cal.get(Calendar.MILLISECOND)
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

//    private fun loadData() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val cal = Calendar.getInstance()
//            val myFormat = "yyyy-MM-dd" // mention the format you need
//            val sdf = SimpleDateFormat(myFormat, Locale.US)
//            val glucose =
//                appDatabase?.bloodGlucoseDao()?.findByToday(sdf.format(cal.time).toString())
//                    ?: return@launch
//            for (gcs: BloodGlucose in glucose) {
//                println("date : ${gcs.millis.toString()}")
//                println("value : ${gcs.bloodSugar.toString()}")
//            }
//        }
//    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}