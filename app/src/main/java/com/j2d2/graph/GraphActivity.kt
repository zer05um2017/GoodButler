package com.j2d2.graph

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.j2d2.R
import com.j2d2.bloodglucose.BloodGlucose
import com.j2d2.main.AppDatabase
import kotlinx.android.synthetic.main.activity_graph.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class GraphActivity : AppCompatActivity(), InvalidateData, OnChartValueSelectedListener {
    private var data: ArrayList<Entry>? = null
    private var appDatabase: AppDatabase? = null
    private var gloucoseListOfDay = listOf<BloodGlucose>()
    private var dayMap = mutableMapOf<Int, String>()
    private var indexOfDay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_graph_title)
        setContentView(R.layout.activity_graph)

        setButtonEvent()
        loadLatestData()
        lineChart.setOnChartValueSelectedListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("chart", this.data)
        super.onSaveInstanceState(outState)
    }

    private fun setDate(curDate: String) {
        textDate.text = curDate
    }

    private fun loadLatestData() {
        appDatabase = AppDatabase.getInstance(this)

        CoroutineScope(Dispatchers.IO).launch {
            val dayOfList = appDatabase?.bloodGlucoseDao()?.getDayList()

            if (dayOfList != null) {
                for ((i, day: String) in dayOfList.withIndex()) {
                    dayMap?.put(i, day)
                }
                val curDate = dayMap[indexOfDay]
                loadData(curDate.toString())
            } else {
                Toast.makeText(
                    this@GraphActivity,
                    getString(R.string.com_j2d2_graph_message_nodata),
                    Toast.LENGTH_LONG
                ).show()
                return@launch
            }
        }
    }

    private fun loadData(curDate:String) {
        appDatabase = AppDatabase.getInstance(this)
        val xValsDateLabel = ArrayList<String>()
        val calendar = GregorianCalendar.getInstance()
        val glucoseList = arrayListOf<Entry>()
        val glucose =
            appDatabase?.bloodGlucoseDao()?.findByToday(curDate)
                ?: return
        gloucoseListOfDay = glucose
        var i = 0f
        if (glucose != null) {
            for (gcs: BloodGlucose in glucose) {
                glucoseList.add(Entry(i, gcs.bloodSugar.toString().toFloat()))
                calendar.timeInMillis = gcs.millis
                xValsDateLabel.add(
                    "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(
                        Calendar.MINUTE
                    )}"
                )
                i++
            }

            val xAxis: XAxis  = lineChart.xAxis
            xAxis.labelCount = glucoseList?.size - 1 ?: 0
            xAxis.valueFormatter = xValsDateLabel?.let { MyValueFormatter(it) }
            lineChart.data = glucoseList?.let { makeLineDataSet(it) }
            invalidate(lineChart = lineChart)
        }
    }

    private fun makeLineDataSet(value: ArrayList<Entry>):LineData {
        val d1 = LineDataSet(value, getString(R.string.com_j2d2_graph_graph_title))
        d1.lineWidth = 2.5f
        d1.circleRadius = 4.5f
        d1.highLightColor = Color.rgb(244, 117, 117)
        d1.setDrawValues(true)

        val sets = arrayListOf<ILineDataSet>()
        sets.add(d1)
        return LineData(sets)
    }

    private fun setButtonEvent() {
        btnNext.setOnClickListener {
            var day = dayMap[--indexOfDay]
            if(day.isNullOrBlank()) {
                Toast.makeText(
                    this@GraphActivity,
                    getString(R.string.com_j2d2_graph_message_last),
                    Toast.LENGTH_SHORT
                ).show()
                indexOfDay++
                return@setOnClickListener
            }
            textInfo.text = ""
            setDate(day.toString())
            CoroutineScope(Dispatchers.IO).launch {
                loadData(day.toString())
            }
        }

        btnPrev.setOnClickListener {
            var day = dayMap[++indexOfDay]
            if(day.isNullOrBlank()) {
                Toast.makeText(
                    this@GraphActivity,
                    getString(R.string.com_j2d2_graph_message_last),
                    Toast.LENGTH_SHORT
                ).show()
                indexOfDay--
                return@setOnClickListener
            }
            textInfo.text = ""
            setDate(day.toString())
            CoroutineScope(Dispatchers.IO).launch {
                loadData(day.toString())
            }
        }
    }

    class MyValueFormatter(private val xValsDateLabel: ArrayList<String>) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return value.toString()
        }

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            if (value.toInt() >= 0 && value.toInt() <= xValsDateLabel.size - 1) {
                var str = xValsDateLabel[value.toInt()].split(":")
                return "%02d:%02d".format(str[0].toInt(), str[1].toInt())
            } else {
                return ("").toString()
            }
        }
    }

    override fun invalidate(lineChart: LineChart) {
        CoroutineScope(Dispatchers.Main).launch {
            lineChart.invalidate()
        }
    }

    override fun onNothingSelected() {}

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        Log.i(
            "VAL SELECTED",
            "Value: " + e!!.y + ", xIndex: " + e!!.x
                    + ", DataSet index: " + h!!.dataSetIndex
        )
        val gloucose = gloucoseListOfDay[e!!.x.toInt()]
        val calendar = GregorianCalendar.getInstance()
        calendar.timeInMillis = gloucose.millis
//        Log.v("#####", calendar.get(Calendar.YEAR).toString())

        textInfo.text = "시간 : %02d시%02d분\n혈당 : %d".format(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), gloucose.bloodSugar)
    }
}