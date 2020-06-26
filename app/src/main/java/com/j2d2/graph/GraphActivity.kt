package com.j2d2.graph

import android.graphics.Color
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
import com.github.mikephil.charting.utils.Utils
import com.j2d2.R
import com.j2d2.bloodglucose.BloodGlucose
import com.j2d2.feeding.Feeding
import com.j2d2.insulin.Insulin
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
    private var insulinListOfDay = listOf<Insulin>()
    private var feedListOfDay = listOf<Feeding>()
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
                loadGlaucoseData(curDate.toString())
                loadCombinedData(curDate.toString())
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

    private fun loadGlaucoseData(curDate:String) {
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

            setLineChartLayout(glucose.size, xValsDateLabel, upperLimited, lowerLimited)

//            combChart.setDrawGridBackground(false)
//            combChart.description.isEnabled = false
//            lineChart.setGridBackgroundColor(0)
            lineChart.description.isEnabled = false
            var lineData = CombinedData()
//            var combData = CombinedData()
            lineData.setData(glucoseList?.let { makeLineDataSet(it) })
//            combData.setData(generateScatterData())
//            combData.setData(generateBarData())
//            combChart.data = combData
            lineChart.data = lineData
            CoroutineScope(Dispatchers.Main).launch {
                lineChart.animateX(500)
//                combChart.animateX(500)
//                invalidate(lineChart = lineChart)
            }
        }
    }

    private fun loadCombinedData(curDate:String) {
//        appDatabase = AppDatabase.getInstance(this)
//        val xValsDateLabel = ArrayList<String>()
//        val calendar = GregorianCalendar.getInstance()
//        val insulinList = arrayListOf<BarEntry>()
//        val feedList = arrayListOf<Entry>()
//        var upperLimited: Float = 0f
//        var lowerLimited: Float = 0f
//        val insulin = appDatabase?.insulinDao()?.findByToday(curDate)?: return
//        val feeding = appDatabase?.feedingDao()?.findByToday(curDate)?: return
//
//        insulinListOfDay = insulin
//        feedListOfDay = feeding
//
//        var i = 0f
//        if (insulin != null) {
//            for (insl: Insulin in insulin) {
//                if(i == 0f) {
//                    upperLimited = insl.totalCapacity.toString().toFloat()
//                    lowerLimited = insl.totalCapacity.toString().toFloat()
//                }
//                insulinList.add(BarEntry(i, insl.totalCapacity.toString().toFloat()))
//
//                if(insl.totalCapacity.toString().toFloat() > upperLimited) {
//                    upperLimited = insl.totalCapacity.toString().toFloat()
//                }
//
//                if(insl.totalCapacity.toString().toFloat() < lowerLimited) {
//                    lowerLimited = insl.totalCapacity.toString().toFloat()
//                }
//
//                calendar.timeInMillis = insl.millis
//                xValsDateLabel.add(
//                    "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(
//                        Calendar.MINUTE
//                    )}"
//                )
//                i++
//            }

//            setLineChartLayout(insulin.size, xValsDateLabel, upperLimited, lowerLimited)


            combChart.setDrawGridBackground(false)
            combChart.description.isEnabled = false
//            lineChart.setGridBackgroundColor(0)
//            var lineData = CombinedData()
            var combData = CombinedData()
//            lineData.setData(insulinList?.let { makeLineDataSet(it) })
            combData.setData(generateBarData(curDate))
            combData.setData(generateScatterData(curDate))
            combChart.data = combData
//            lineChart.data = lineData
            CoroutineScope(Dispatchers.Main).launch {
//                lineChart.animateX(500)
                combChart.animateX(500)
//                invalidate(lineChart = lineChart)
            }
//        }
    }

    private fun setLineChartLayout(size: Int, list:ArrayList<String>, upperLimited:Float, lowerLimited: Float) {
        val xAxis: XAxis  = lineChart.xAxis
        xAxis.labelCount = size - 1 ?: 0
        xAxis.valueFormatter = list?.let { MyValueFormatter(it) }
        // vertical grid lines
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.gridColor = Color.rgb(211,214,219)
        var yAxis: YAxis
        yAxis = lineChart.getAxisLeft()
        yAxis.gridColor = Color.rgb(211,214,219)
        // disable dual axis (only use LEFT axis)
        lineChart.getAxisRight().setEnabled(false)
        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f)
        // axis range
        yAxis.axisMaximum = upperLimited + 20f
        yAxis.axisMinimum = lowerLimited - 20f

        val llXAxis = LimitLine(9f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f
        val ll1 = LimitLine(upperLimited, "최대")
        ll1.lineWidth = 1f
        ll1.lineColor = Color.rgb(46,190,197)
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f
        val ll2 = LimitLine(lowerLimited, "최저")
        ll2.lineWidth = 1f
        ll2.lineColor = Color.rgb(46,190,197)
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
        lineChart.description.isEnabled = false
    }

    private fun setCombChartLayout(size: Int, list:ArrayList<String>, upperLimited:Float, lowerLimited: Float) {
        val xAxis: XAxis  = combChart.xAxis
        xAxis.labelCount = size - 1 ?: 0
        xAxis.valueFormatter = list?.let { MyValueFormatter(it) }
        // vertical grid lines
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.gridColor = Color.rgb(211,214,219)
        var yAxis: YAxis
        yAxis = combChart.getAxisLeft()
        yAxis.gridColor = Color.rgb(211,214,219)
        // disable dual axis (only use LEFT axis)
        combChart.getAxisRight().setEnabled(false)
        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f)
        // axis range
        yAxis.axisMaximum = upperLimited + 20f
        yAxis.axisMinimum = lowerLimited - 20f

//        val llXAxis = LimitLine(9f, "Index 10")
//        llXAxis.lineWidth = 4f
//        llXAxis.enableDashedLine(10f, 10f, 0f)
//        llXAxis.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
//        llXAxis.textSize = 10f
//        val ll1 = LimitLine(upperLimited, "최대")
//        ll1.lineWidth = 1f
//        ll1.lineColor = Color.rgb(46,190,197)
//        ll1.enableDashedLine(10f, 10f, 0f)
//        ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
//        ll1.textSize = 10f
//        val ll2 = LimitLine(lowerLimited, "최저")
//        ll2.lineWidth = 1f
//        ll2.lineColor = Color.rgb(46,190,197)
//        ll2.enableDashedLine(10f, 10f, 0f)
//        ll2.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
//        ll2.textSize = 10f

        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines
        yAxis.removeAllLimitLines()
//        yAxis.addLimitLine(ll1)
//        yAxis.addLimitLine(ll2)
        //xAxis.addLimitLine(llXAxis);
        combChart.setDrawGridBackground(false)
        combChart.description.isEnabled = false
    }

    private fun makeLineDataSet(value: ArrayList<Entry>):LineData {
        val set = LineDataSet(value, getString(R.string.com_j2d2_graph_graph_title))
        set.setDrawIcons(false)
        // draw dashed line
//        set1.enableDashedLine(10f, 5f, 0f)
        // black lines and points
        set.setColor(Color.rgb(247, 153, 164))
        set.setValueTextColors(arrayListOf(Color.DKGRAY))
//        set.setCircleColor(Color.DKGRAY)
        // line thickness and point size
        set.setLineWidth(1f)
//        set1.circleHoleRadius = 3f
        set.setCircleRadius(4f)
        set.setCircleColor(Color.rgb(221,82,117))
        // draw points as solid circles
        set.setDrawCircleHole(true)
        // customize legend entry
        set.setFormLineWidth(1f)

//        set1.setFormLineDashEffect(DashPathEffect(floatArrayOf(10f, 5f), 0f))
        set.setFormSize(15f)
        // text size of values
        set.setValueTextSize(12f)
        // draw selection line as dashed
//        set1.enableDashedHighlightLine(10f, 5f, 0f)
        // set the filled area
        set.setDrawFilled(true)
        set.setFillFormatter(IFillFormatter { dataSet, dataProvider ->
            lineChart.getAxisLeft().getAxisMinimum()
        })
        // set color of filled area
        if (Utils.getSDKInt() >= 18) {
            // drawables only supported on api level 18 and above
            val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
            set.setFillDrawable(drawable)
        } else {
            set.setFillColor(Color.BLACK)
        }

        val dataSets = arrayListOf<ILineDataSet>()
        dataSets.add(set) // add the data sets
        return LineData(dataSets)
    }

    private fun generateScatterData(curDate:String): ScatterData? {

        var d = ScatterData();
//
//        var entries = arrayListOf<Entry>()
//
//        for (index in 0 until 2)
//            entries.add(Entry(0.5f + (index * 8), ((Math.random()* 100) + 55).toFloat()));
        appDatabase = AppDatabase.getInstance(this)
//        val xValsDateLabel = ArrayList<String>()
        val calendar = GregorianCalendar.getInstance()
//        val insulinList = arrayListOf<BarEntry>()
        val feedList = arrayListOf<Entry>()
//        var upperLimited: Float = 0f
//        var lowerLimited: Float = 0f
        val feeding = appDatabase?.feedingDao()?.findByToday(curDate)?: return null

//        insulinListOfDay = insulin
        feedListOfDay = feeding

        var i = 0f
        if (feeding != null) {
            for (fed: Feeding in feeding) {
//                if (i == 0f) {
//                    upperLimited = insl.totalCapacity.toString().toFloat()
//                    lowerLimited = insl.totalCapacity.toString().toFloat()
//                }
                feedList.add(BarEntry(i, fed.feedingAmount.toString().toFloat()))

//                if (insl.totalCapacity.toString().toFloat() > upperLimited) {
//                    upperLimited = insl.totalCapacity.toString().toFloat()
//                }
//
//                if (insl.totalCapacity.toString().toFloat() < lowerLimited) {
//                    lowerLimited = insl.totalCapacity.toString().toFloat()
//                }

//                calendar.timeInMillis = insl.millis
//                xValsDateLabel.add(
//                    "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(
//                        Calendar.MINUTE
//                    )}"
//                )
                i++
            }
        }


        var set = ScatterDataSet(feedList, "인슐린");
        set.setColors(Color.rgb(236,32,8))
        set.setScatterShapeSize(10f);
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        d.addDataSet(set);

        return d;
    }

    private fun generateBarData(curDate:String): BarData? {
//        val entries1 = java.util.ArrayList<BarEntry>()
////        val entries2 = java.util.ArrayList<BarEntry>()
//        for (index in 0 until 2) {
//            entries1.add(BarEntry(0.1f + (index * 7), ((Math.random()* 100) + 55).toFloat()))
//
//            // stacked
////            entries2.add(BarEntry(0.0f, floatArrayOf(((Math.random()* 100) + 55).toFloat(), ((Math.random()* 100) + 55).toFloat())))
//        }
        appDatabase = AppDatabase.getInstance(this)
        val xValsDateLabel = ArrayList<String>()
        val calendar = GregorianCalendar.getInstance()
        val insulinList = arrayListOf<BarEntry>()
//        val feedList = arrayListOf<Entry>()
        var upperLimited: Float = 0f
        var lowerLimited: Float = 0f
        val insulin = appDatabase?.insulinDao()?.findByToday(curDate)?: return null
//        val feeding = appDatabase?.feedingDao()?.findByToday(curDate)?: return null

        insulinListOfDay = insulin
//        feedListOfDay = feeding

        var i = 0f
        if (insulin != null) {
            for (insl: Insulin in insulin) {
                if (i == 0f) {
                    upperLimited = insl.totalCapacity.toString().toFloat()
                    lowerLimited = insl.totalCapacity.toString().toFloat()
                }
                insulinList.add(BarEntry(i, insl.totalCapacity.toString().toFloat()))

                if (insl.totalCapacity.toString().toFloat() > upperLimited) {
                    upperLimited = insl.totalCapacity.toString().toFloat()
                }

                if (insl.totalCapacity.toString().toFloat() < lowerLimited) {
                    lowerLimited = insl.totalCapacity.toString().toFloat()
                }

//                calendar.timeInMillis = insl.millis
//                xValsDateLabel.add(
//                    "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(
//                        Calendar.MINUTE
//                    )}"
//                )
                i++
            }
        }

        val timeLineList = appDatabase?.graphDao()?.timeLineData(curDate)?: return null

        if(timeLineList != null) {
            for(date:Long in timeLineList) {
                calendar.timeInMillis = date
                xValsDateLabel.add(
                    "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(
                        Calendar.MINUTE
                    )}"
                )
            }
        }

        setCombChartLayout(insulin.size, xValsDateLabel, upperLimited, lowerLimited)

        val set = BarDataSet(insulinList, "사료급여")
        set.color = Color.rgb(93, 192, 158)
        set.valueTextColor = Color.rgb(15, 97, 142)
        set.valueTextSize = 10f
        set.axisDependency = YAxis.AxisDependency.LEFT
//        val set2 = BarDataSet(entries2, "")
//        set2.stackLabels = arrayOf("Stack 1", "Stack 2")
//        set2.setColors(
//            Color.rgb(61, 165, 255),
//            Color.rgb(23, 197, 255)
//        )
//        set2.valueTextColor = Color.rgb(61, 165, 255)
//        set2.valueTextSize = 10f
//        set2.axisDependency = YAxis.AxisDependency.LEFT
//        val groupSpace = 0.06f
//        val barSpace = 0.02f // x2 dataset
//        val barWidth = 0.45f // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"
//        val d = BarData(set1, set2)
//        val d = BarData(set)
//        d.barWidth = barWidth

        // make this BarData object grouped
//        d.groupBars(0f, groupSpace, barSpace) // start at x = 0
        return BarData(set)
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
                loadGlaucoseData(day.toString())
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
                loadGlaucoseData(day.toString())
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