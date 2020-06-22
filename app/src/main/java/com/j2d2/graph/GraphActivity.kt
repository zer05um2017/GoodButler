package com.j2d2.graph

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.j2d2.R
import com.j2d2.bloodglucose.BloodGlucose
import com.j2d2.main.AppDatabase
import kotlinx.android.synthetic.main.activity_graph.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GraphActivity : AppCompatActivity() {
    private var data: ArrayList<Entry>? = null
    private var appDatabase: AppDatabase? = null
    private val values1 = arrayListOf<Entry>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        appDatabase = AppDatabase.getInstance(this)

        if(savedInstanceState != null) {
            this.data = savedInstanceState.getSerializable("chart") as ArrayList<Entry>?
        } else {

            CoroutineScope(Dispatchers.IO).launch {
                val cal = Calendar.getInstance()
                val myFormat = "yyyyMMdd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val glucose = appDatabase?.bloodGlucoseDao()?.findByToday(sdf.format(cal.time).toString())?: return@launch
                if (glucose != null) {
                    for (gcs: BloodGlucose in glucose) {
                        values1.add(Entry(gcs.time.toFloat(), gcs.bloodSugar.toString().toFloat()))
                        println("${gcs.uid} => date : ${gcs.date.toString()}")
                        println("${gcs.uid} => time : ${gcs.time.toString()}")
                        println("${gcs.uid} => value : ${gcs.bloodSugar.toString()}")
                    }
                }
            }
            this.data = values1
//            this.data = generateDataLine(1)
        }

        lineChart.data = this.data?.let { makeLineDataSet(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("chart", this.data)
        super.onSaveInstanceState(outState)
    }

    private fun makeLineDataSet(value: ArrayList<Entry>):LineData {
        val d1 = LineDataSet(value, "New DataSet (1)")
        d1.lineWidth = 2.5f
        d1.circleRadius = 4.5f
        d1.highLightColor = Color.rgb(244, 117, 117)
        d1.setDrawValues(true)
        val sets = arrayListOf<ILineDataSet>()
        sets.add(d1)
        return LineData(sets)
    }

    private fun generateDataLine(cnt: Int): ArrayList<Entry>? {
//        val values1 = arrayListOf<Entry>()
//
//        for(i:Int in 1..12) {
//            values1.add(Entry(i.toFloat(), ((Math.random() * 65) + 40).toFloat()))
//        }
//        this.data = values1
//        return values1
        val values1 = arrayListOf<Entry>()
        CoroutineScope(Dispatchers.IO).launch {
            val cal = Calendar.getInstance()
            val myFormat = "yyyyMMdd" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val glucose = appDatabase?.bloodGlucoseDao()?.findByToday(sdf.format(cal.time).toString())?: return@launch
            if (glucose != null) {
                for (gcs: BloodGlucose in glucose) {
                    values1.add(Entry(gcs.time.toFloat(), gcs.bloodSugar.toString().toFloat()))
                    println("${gcs.uid} => date : ${gcs.date.toString()}")
                    println("${gcs.uid} => time : ${gcs.time.toString()}")
                    println("${gcs.uid} => value : ${gcs.bloodSugar.toString()}")
                }
            }
        }
        this.data = values1
        return values1
    }
}