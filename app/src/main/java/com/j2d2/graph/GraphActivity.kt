package com.j2d2.graph

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
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
        supportActionBar?.hide()
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
        var upperLimited: Float = 0f
        var lowerLimited: Float = 0f
        val glucose =
            appDatabase?.bloodGlucoseDao()?.findByToday(curDate)
                ?: return
        gloucoseListOfDay = glucose
        var i = 0f
        if (glucose != null) {
            for (gcs: BloodGlucose in glucose) {
                if(i == 0f) {
                    upperLimited = gcs.bloodSugar.toString().toFloat()
                    lowerLimited = gcs.bloodSugar.toString().toFloat()
                }
                glucoseList.add(Entry(i, gcs.bloodSugar.toString().toFloat()))

                if(gcs.bloodSugar.toString().toFloat() > upperLimited) {
                    upperLimited = gcs.bloodSugar.toString().toFloat()
                }

                if(gcs.bloodSugar.toString().toFloat() < lowerLimited) {
                    lowerLimited = gcs.bloodSugar.toString().toFloat()
                }

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
            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f)
            var yAxis: YAxis
            yAxis = lineChart.getAxisLeft()
            // disable dual axis (only use LEFT axis)
            lineChart.getAxisRight().setEnabled(false)
            // horizontal grid lines
            yAxis.enableGridDashedLine(10f, 10f, 0f)
            // axis range
            yAxis.axisMaximum = upperLimited + 50f
            yAxis.axisMinimum = lowerLimited - 30f

            val llXAxis = LimitLine(9f, "Index 10")
            llXAxis.lineWidth = 4f
            llXAxis.enableDashedLine(10f, 10f, 0f)
            llXAxis.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
            llXAxis.textSize = 10f
            val ll1 = LimitLine(upperLimited, "최대")
            ll1.lineWidth = 4f
            ll1.enableDashedLine(10f, 10f, 0f)
            ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
            ll1.textSize = 10f
            val ll2 = LimitLine(lowerLimited, "최저")
            ll2.lineWidth = 4f
            ll2.enableDashedLine(10f, 10f, 0f)
            ll2.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
            ll2.textSize = 10f

            // draw limit lines behind data instead of on top
            yAxis.setDrawLimitLinesBehindData(true)
            xAxis.setDrawLimitLinesBehindData(true)

            // add limit lines
            yAxis.removeAllLimitLines()
            yAxis.addLimitLine(ll1)
            yAxis.addLimitLine(ll2)
            //xAxis.addLimitLine(llXAxis);
            lineChart.setDrawGridBackground(false)
//            lineChart.setGridBackgroundColor(0)
            lineChart.data = glucoseList?.let { makeLineDataSet(it) }
            CoroutineScope(Dispatchers.Main).launch {
                lineChart.animateX(500)
//                invalidate(lineChart = lineChart)
            }
        }
    }

    private fun makeLineDataSet(value: ArrayList<Entry>):LineData {
        val set1 = LineDataSet(value, getString(R.string.com_j2d2_graph_graph_title))
        set1.setDrawIcons(false)
        // draw dashed line
        set1.enableDashedLine(10f, 5f, 0f)
        // black lines and points
        set1.setColor(Color.BLACK)
        set1.setCircleColor(Color.BLACK)
        // line thickness and point size
        set1.setLineWidth(1f)
        set1.setCircleRadius(3f)
        // draw points as solid circles
        set1.setDrawCircleHole(false)
        // customize legend entry
        set1.setFormLineWidth(1f)
        set1.setFormLineDashEffect(DashPathEffect(floatArrayOf(10f, 5f), 0f))
        set1.setFormSize(15f)
        // text size of values
        set1.setValueTextSize(9f)
        // draw selection line as dashed
        set1.enableDashedHighlightLine(10f, 5f, 0f)
        // set the filled area
        set1.setDrawFilled(true)
        set1.setFillFormatter(IFillFormatter { dataSet, dataProvider ->
            lineChart.getAxisLeft().getAxisMinimum()
        })
        // set color of filled area
        if (Utils.getSDKInt() >= 18) {
            // drawables only supported on api level 18 and above
            val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
            set1.setFillDrawable(drawable)
        } else {
            set1.setFillColor(Color.BLACK)
        }

        val dataSets = arrayListOf<ILineDataSet>()
        dataSets.add(set1) // add the data sets
        return LineData(dataSets)
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