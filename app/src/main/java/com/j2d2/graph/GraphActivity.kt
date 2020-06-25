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
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
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
        combChart.setOnChartValueSelectedListener(this)
//        testData1()
//        testData2()
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

            val xAxis: XAxis  = combChart.xAxis
            xAxis.labelCount = glucoseList?.size - 1 ?: 0
            xAxis.valueFormatter = xValsDateLabel?.let { MyValueFormatter(it) }
            // vertical grid lines
            xAxis.enableGridDashedLine(10f, 10f, 0f)
            var yAxis: YAxis
            yAxis = combChart.getAxisLeft()
            // disable dual axis (only use LEFT axis)
            combChart.getAxisRight().setEnabled(false)
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
            combChart.setDrawGridBackground(false)
//            lineChart.setGridBackgroundColor(0)
            var combData = CombinedData()
            combData.setData(glucoseList?.let { makeLineDataSet(it) })
            combData.setData(generateScatterData())
            combData.setData(generateBarData())
            combChart.data = combData
            CoroutineScope(Dispatchers.Main).launch {
                combChart.animateX(500)
//                invalidate(lineChart = lineChart)
            }
        }
    }

//    private fun testData1() {
//        barChartForInsulin.getDescription().setEnabled(false)
//
//        // if more than 60 entries are displayed in the chart, no values will be
//        // drawn
//
//        // if more than 60 entries are displayed in the chart, no values will be
//        // drawn
//        barChartForInsulin.setMaxVisibleValueCount(60)
//
//        // scaling can now only be done on x- and y-axis separately
//
//        // scaling can now only be done on x- and y-axis separately
//        barChartForInsulin.setPinchZoom(false)
//
//        barChartForInsulin.setDrawBarShadow(false)
//        barChartForInsulin.setDrawGridBackground(false)
//
//        val xAxis: XAxis = barChartForInsulin.getXAxis()
//        xAxis.position = XAxisPosition.BOTTOM
//        xAxis.setDrawGridLines(false)
//
//        barChartForInsulin.getAxisLeft().setDrawGridLines(false)
//
//        // setting data
//
//        // setting data
//        val values = arrayListOf<BarEntry>()
//
//        for (i in 0 until 2) {
//            val multi: Float = (2 + 1).toFloat()
//            val val1 = (Math.random() * multi).toFloat() + multi / 3
//            val barE = BarEntry(i.toFloat(), val1)
//            values.add(barE)
//        }
//
//        val set1: BarDataSet
//
//        if (barChartForInsulin.getData() != null &&
//            barChartForInsulin.getData().getDataSetCount() > 0
//        ) {
//            set1 =
//                barChartForInsulin.data.getDataSetByIndex(0) as BarDataSet// .getData().getDataSetByIndex(0)
//            set1.values = values
//            barChartForInsulin.getData().notifyDataChanged()
//            barChartForInsulin.notifyDataSetChanged()
//        } else {
//            set1 = BarDataSet(values, "Data Set")
//            set1.setColors(*ColorTemplate.VORDIPLOM_COLORS)
//            set1.setDrawValues(false)
//            val dataSets = java.util.ArrayList<IBarDataSet>()
//            dataSets.add(set1)
//            val data = BarData(dataSets)
//            barChartForInsulin.setData(data)
//            barChartForInsulin.setFitBars(true)
//        }
//
//        barChartForInsulin.invalidate()
//        // add a nice and smooth animation
//
//        // add a nice and smooth animation
//        barChartForInsulin.animateY(500)
//
//        barChartForInsulin.getLegend().setEnabled(false)
//    }
//
//    private fun testData2() {
//        barChartForFeeding.getDescription().setEnabled(false)
//
//        // if more than 60 entries are displayed in the chart, no values will be
//        // drawn
//
//        // if more than 60 entries are displayed in the chart, no values will be
//        // drawn
//        barChartForFeeding.setMaxVisibleValueCount(60)
//
//        // scaling can now only be done on x- and y-axis separately
//
//        // scaling can now only be done on x- and y-axis separately
//        barChartForFeeding.setPinchZoom(false)
//
//        barChartForFeeding.setDrawBarShadow(false)
//        barChartForFeeding.setDrawGridBackground(false)
//
//        val xAxis: XAxis = barChartForFeeding.getXAxis()
//        xAxis.position = XAxisPosition.BOTTOM
//        xAxis.setDrawGridLines(false)
//
//        barChartForFeeding.getAxisLeft().setDrawGridLines(false)
//
//        // setting data
//
//        // setting data
//        val values = arrayListOf<BarEntry>()
//
//        for (i in 0 until 2) {
//            val multi: Float = (2 + 1).toFloat()
//            val val1 = (Math.random() * multi).toFloat() + multi / 3
//            val barE = BarEntry(i.toFloat(), val1)
//            values.add(barE)
//        }
//
//        val set1: BarDataSet
//
//        if (barChartForFeeding.getData() != null &&
//            barChartForFeeding.getData().getDataSetCount() > 0
//        ) {
//            set1 =
//                barChartForFeeding.data.getDataSetByIndex(0) as BarDataSet// .getData().getDataSetByIndex(0)
//            set1.values = values
//            barChartForFeeding.getData().notifyDataChanged()
//            barChartForFeeding.notifyDataSetChanged()
//        } else {
//            set1 = BarDataSet(values, "Data Set")
//            set1.setColors(*ColorTemplate.VORDIPLOM_COLORS)
//            set1.setDrawValues(false)
//            val dataSets = java.util.ArrayList<IBarDataSet>()
//            dataSets.add(set1)
//            val data = BarData(dataSets)
//            barChartForFeeding.setData(data)
//            barChartForFeeding.setFitBars(true)
//        }
//
//        barChartForFeeding.invalidate()
//        // add a nice and smooth animation
//
//        // add a nice and smooth animation
//        barChartForFeeding.animateY(500)
//
//        barChartForFeeding.getLegend().setEnabled(false)
//    }

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
            combChart.getAxisLeft().getAxisMinimum()
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

    private fun generateScatterData():ScatterData {

        var d = ScatterData();

        var entries = arrayListOf<Entry>()

        for (i in 0..8)
            entries.add(Entry(i + 0.25f, ((Math.random()* 100) + 55).toFloat()));

        var set = ScatterDataSet(entries, "Scatter DataSet");
        set.setColors(*ColorTemplate.MATERIAL_COLORS)
        set.setScatterShapeSize(7.5f);
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        d.addDataSet(set);

        return d;
    }

    private fun generateBarData(): BarData? {
        val entries1 = java.util.ArrayList<BarEntry>()
        val entries2 = java.util.ArrayList<BarEntry>()
        for (index in 0 until 8) {
            entries1.add(BarEntry(0.0f, ((Math.random()* 100) + 55).toFloat()))

            // stacked
            entries2.add(BarEntry(0.0f, floatArrayOf(((Math.random()* 100) + 55).toFloat(), ((Math.random()* 100) + 55).toFloat())))
        }
        val set1 = BarDataSet(entries1, "Bar 1")
        set1.color = Color.rgb(60, 220, 78)
        set1.valueTextColor = Color.rgb(60, 220, 78)
        set1.valueTextSize = 10f
        set1.axisDependency = YAxis.AxisDependency.LEFT
        val set2 = BarDataSet(entries2, "")
        set2.stackLabels = arrayOf("Stack 1", "Stack 2")
        set2.setColors(
            Color.rgb(61, 165, 255),
            Color.rgb(23, 197, 255)
        )
        set2.valueTextColor = Color.rgb(61, 165, 255)
        set2.valueTextSize = 10f
        set2.axisDependency = YAxis.AxisDependency.LEFT
        val groupSpace = 0.06f
        val barSpace = 0.02f // x2 dataset
        val barWidth = 0.45f // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"
        val d = BarData(set1, set2)
        d.barWidth = barWidth

        // make this BarData object grouped
        d.groupBars(0f, groupSpace, barSpace) // start at x = 0
        return d
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