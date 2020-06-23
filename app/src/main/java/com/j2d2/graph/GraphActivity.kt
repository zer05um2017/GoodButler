package com.j2d2.graph

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.j2d2.R
import com.j2d2.bloodglucose.BloodGlucose
import com.j2d2.main.AppDatabase
import kotlinx.android.synthetic.main.activity_graph.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GraphActivity : AppCompatActivity() {
    private var data: ArrayList<Entry>? = null
    private var appDatabase: AppDatabase? = null
    private val values1 = arrayListOf<Entry>()
    private val xValsDateLabel: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        appDatabase = AppDatabase.getInstance(this)
        val xValsDateLabel = ArrayList<String>()

        if(savedInstanceState != null) {
            this.data = savedInstanceState.getSerializable("chart") as ArrayList<Entry>?
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val cal = Calendar.getInstance()
                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val calendar = GregorianCalendar.getInstance()
                val glucose =
                    appDatabase?.bloodGlucoseDao()?.findByToday(sdf.format(cal.time).toString())
                        ?: return@launch
                var i: Float = 0f
                if (glucose != null) {
                    for (gcs: BloodGlucose in glucose) {
                        values1.add(Entry(i.toFloat(), gcs.bloodSugar.toString().toFloat()))
                        calendar.timeInMillis = gcs.millis
                        xValsDateLabel.add(
                            "${calendar.get(Calendar.HOUR_OF_DAY).toString()}:${calendar.get(
                                Calendar.MINUTE
                            ).toString()}"
                        )
                        i++
                    }

                }
                Log.d("###","in Coroutine")
                var data = values1
                val xAxis: XAxis  = lineChart.xAxis
                xAxis.labelCount = data?.size - 1 ?: 0
                xAxis.valueFormatter = xValsDateLabel?.let { MyValueFormatter(it) }//(xValsDateLabel?.let { MyValueFormatter(it) })
                lineChart.data = data?.let { makeLineDataSet(it) }
            }
            Log.d("###","out of Coroutine")
//            this.data = values1
//            val xAxis: XAxis  = lineChart.xAxis
//            xAxis.labelCount = this.data?.size ?: 0
//            xAxis.valueFormatter = xValsDateLabel?.let { MyValueFormatter(it) }//(xValsDateLabel?.let { MyValueFormatter(it) })
//            lineChart.data = this.data?.let { makeLineDataSet(it) }
        }
    }

    override fun onStart() {
        super.onStart()
//        val xAxis: XAxis  = lineChart.xAxis
//        xAxis.labelCount = this.data?.size ?: 0
//        xAxis.valueFormatter = xValsDateLabel?.let { MyValueFormatter(it) }//(xValsDateLabel?.let { MyValueFormatter(it) })
//        lineChart.data = this.data?.let { makeLineDataSet(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("chart", this.data)
        super.onSaveInstanceState(outState)
    }

    private fun makeLineDataSet(value: ArrayList<Entry>):LineData {
        val d1 = LineDataSet(value, "혈당 그래프")
        d1.lineWidth = 2.5f
        d1.circleRadius = 4.5f
        d1.highLightColor = Color.rgb(244, 117, 117)
        d1.setDrawValues(true)

        val sets = arrayListOf<ILineDataSet>()
        sets.add(d1)
        return LineData(sets)
    }

//    private fun generateDataLine(cnt: Int): ArrayList<Entry>? {
//        val values1 = arrayListOf<Entry>()
//        CoroutineScope(Dispatchers.IO).launch {
//            val calendar = GregorianCalendar.getInstance()
//
//            Log.v("#####", calendar.get(Calendar.YEAR).toString())
//            Log.v("#####", calendar.get(Calendar.MONTH).toString())
//            Log.v("#####", calendar.get(Calendar.DAY_OF_MONTH).toString())
//            Log.v("#####", calendar.get(Calendar.HOUR_OF_DAY).toString())
//            Log.v("#####", calendar.get(Calendar.MINUTE).toString())
//            val cal = Calendar.getInstance()
//            val myFormat = "yyyyMMdd" // mention the format you need
//            val sdf = SimpleDateFormat(myFormat, Locale.US)
//            val glucose = appDatabase?.bloodGlucoseDao()?.findByToday(sdf.format(cal.time).toString())?: return@launch
//            if (glucose != null) {
//                for (gcs: BloodGlucose in glucose) {
//                    calendar.timeInMillis = gcs.millis
//                    values1.add(Entry(${calendar.get(Calendar.HOUR_OF_DAY).toString()}:${calendar.get(Calendar.MINUTE).toString()}, gcs.bloodSugar.toString().toFloat()))
//                    println("${gcs.uid} => date : ${gcs.date.toString()}")
//                    println("${gcs.uid} => time : ${gcs.time.toString()}")
//                    println("${gcs.uid} => value : ${gcs.bloodSugar.toString()}")
//                }
//            }
//        }
//        this.data = values1
//        return values1
//    }

    class MyValueFormatter(private val xValsDateLabel: ArrayList<String>) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return value.toString()
        }

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            if (value.toInt() >= 0 && value.toInt() <= xValsDateLabel.size - 1) {
                return xValsDateLabel[value.toInt()]
            } else {
                return ("").toString()
            }
        }
    }
}