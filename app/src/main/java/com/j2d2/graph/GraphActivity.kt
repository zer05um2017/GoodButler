package com.j2d2.graph

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import com.j2d2.R
import com.j2d2.bloodglucose.BloodGlucose
import com.j2d2.bloodglucose.BloodGlucoseActivity
import com.j2d2.bloodglucose.BloodGlucoseParcel
import com.j2d2.feeding.FeedParcel
import com.j2d2.feeding.Feeding
import com.j2d2.feeding.FeedingActivity
import com.j2d2.insulin.Insulin
import com.j2d2.insulin.InsulinActivity
import com.j2d2.insulin.InsulinParcel
import com.j2d2.main.*
import kotlinx.android.synthetic.main.activity_graph.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class GraphActivity : AppCompatActivity(),
    InvalidateData,
    OnChartValueSelectedListener,
    OnSelectedDataCallBack,
    DialogOnClickListener {
    private var data: ArrayList<Entry>? = null
    private var appDatabase: AppDatabase? = null
    private var gloucoseListOfDay = listOf<BloodGlucose>()
    private var timeLineOfDay = listOf<GraphTimeLine>()
//    private var dayMap = mutableMapOf<Int, String>()
    private var daysOfMonthMap = arrayListOf<ArrayList<String>>()
    private var indexOfMonth: Int = 0
    private var indexOfDay: Int = 0
    private lateinit var selectedParcel:Terry
    private var selectedDataType: DataType = DataType.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.com_j2d2_graph_title)
        setContentView(R.layout.activity_graph)
        supportActionBar?.hide()
        appDatabase = AppDatabase.getInstance(this)
        setButtonEvent()
        loadData()
        lineChart.setOnChartValueSelectedListener(this)
        textInfo.movementMethod = ScrollingMovementMethod()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("chart", this.data)
        super.onSaveInstanceState(outState)
    }

    private fun setDate(curDate: String) {
        CoroutineScope(Dispatchers.Main).launch {
            textDate.text = curDate
        }
    }

    private fun getToday(): String {
        val calendar = GregorianCalendar.getInstance()
        calendar.timeZone
//        Log.v("#####", calendar.get(Calendar.YEAR).toString())
//        Log.v("#####", calendar.get(Calendar.MONTH).toString())
        return "${calendar.get(Calendar.YEAR)}${calendar.get(Calendar.MONTH)}"
    }

    private fun loadData() {
        lineChart.invalidate()
        combChart.invalidate()
        CoroutineScope(Dispatchers.IO).launch {
            // load all months in the existent data
            val months = appDatabase?.graphDao()?.getAllMonthsList()!!

            for(i in months.indices) {
                val days = appDatabase?.graphDao()?.getDayListOfMonth(month = months[i])!!
                var temp = ArrayList<String>()
                for ((j, day: String) in days.withIndex()) {
                    temp.add(day)
                }
                daysOfMonthMap.add(temp)
            }
            try{
//                CoroutineScope(Dispatchers.Main).launch {
//                    lineChart.invalidate()
//                    combChart.invalidate()
//                }
                val curDate = daysOfMonthMap[indexOfMonth][indexOfDay]
                setDate(curDate)
                loadGlaucoseData(curDate)
                loadCombinedData(curDate)
            } catch (e: IndexOutOfBoundsException) {
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
        var lineData = CombinedData()
        val glucose =
            appDatabase?.bloodGlucoseDao()?.findByToday(curDate)
                ?: return
        gloucoseListOfDay = glucose

        for ((i, gcs: BloodGlucose) in glucose?.withIndex()) {
            if(i == 0) {
                upperLimited = gcs.bloodSugar.toString().toFloat()
                lowerLimited = gcs.bloodSugar.toString().toFloat()
            }
            glucoseList.add(Entry(i.toFloat(), gcs.bloodSugar.toString().toFloat()))

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
        }

        // get the legend (only possible after setting data)
        val l: Legend = lineChart.getLegend()
        // modify the legend ...
        l.form = LegendForm.CIRCLE
        l.setDrawInside(true)
        l.formSize = 15f
        l.textSize = 15f
        lineChart.description.isEnabled = false
        lineData.setData(glucoseList?.let { makeLineDataSet(it) })
        lineChart.data = lineData

        setLineChartLayout(xValsDateLabel, upperLimited, lowerLimited)
        var millis = (1000 * (glucoseList.size * 0.1f)).toInt()
        CoroutineScope(Dispatchers.Main).launch {
            lineChart.animateX(if(millis > 500) 500 else millis)
        }
    }

    private fun loadCombinedData(curDate:String) {
        var combData = CombinedData()
        var d = ScatterData();
        appDatabase = AppDatabase.getInstance(this)
        val xValsDateLabel = ArrayList<String>()
        val calendar = GregorianCalendar.getInstance()
        var upperLimited: Float = 0f
        var lowerLimited: Float = 0f
        val values0 = arrayListOf<Entry>()
        val values1 = arrayListOf<Entry>()
        var timeLine = appDatabase?.graphDao()?.timeLineData(curDate)?: return

        timeLineOfDay = timeLine

        for ((i, day: GraphTimeLine) in timeLine?.withIndex()) {
            if(i == 0) {
                upperLimited = day.totalCapacity.toString().toFloat()
                lowerLimited = day.totalCapacity.toString().toFloat()
            }

            if(day.totalCapacity.toString().toFloat() > upperLimited) {
                upperLimited = day.totalCapacity.toString().toFloat()
            }

            if(day.totalCapacity.toString().toFloat() < lowerLimited) {
                lowerLimited = day.totalCapacity.toString().toFloat()
            }

            when(day.dataType) {
                0 -> {
                    values0.add(Entry(i.toFloat(), day.totalCapacity.toString().toFloat()))
                }

                1 ->
                {
                    values1.add(Entry(i.toFloat(), day.totalCapacity.toString().toFloat()))
                }
            }

            calendar.timeInMillis = day.millis
            xValsDateLabel.add(
                "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(
                    Calendar.MINUTE
                )}"
            )
        }

        // create a dataset and give it a type
        val set1 = ScatterDataSet(values0, "사료")
        set1.setColors(Color.rgb(245,161,27))
        set1.setValueTextColors(arrayListOf(Color.DKGRAY))
        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE)
        set1. scatterShapeSize = 15f
        set1.setDrawValues(true)
        set1.valueTextSize = 15f

        val set2 = ScatterDataSet(values1, "인슐린")
        set2.setColors(Color.rgb(236,32,8))
        set2.setValueTextColors(arrayListOf(Color.DKGRAY))
        set2.scatterShapeSize = 15f
        set2.setDrawValues(true)
//        set1.setDrawIcons(true)
        set2.valueTextSize = 15f
//        set2.scatterShapeHoleRadius = 4f

        d.addDataSet(set1)
        d.addDataSet(set2)

        combChart.setDrawGridBackground(true)
        combChart.description.isEnabled = false
        combData.setData(d)
        combChart.data = combData
        setCombChartLayout(xValsDateLabel, upperLimited, lowerLimited)
        combChart.setOnChartValueSelectedListener(MyScatterChartTouchListener(timeLineOfDay, textInfo, appDatabase, this))
        combChart.isDoubleTapToZoomEnabled = false
        combChart.setPinchZoom(false)

        CoroutineScope(Dispatchers.Main).launch {
            combChart.animateY(500)
        }
    }

    private fun setLineChartLayout(list:ArrayList<String>, upperLimited:Float, lowerLimited: Float) {
        val xAxis: XAxis  = lineChart.xAxis
//        xAxis.resetAxisMaximum()
//        xAxis.resetAxisMinimum()
//        xAxis.labelCount = list.size - 1 ?: 0

        when(list.size) {
            1 -> {
                xAxis.labelCount = list.size
                xAxis.axisMaximum = lineChart.xChartMax + 0.1f
                xAxis.axisMinimum = lineChart.xChartMin - 0.1f
            }
            2 -> {
                xAxis.setLabelCount(list.size, true)
                xAxis.axisMaximum = lineChart.xChartMax + 0.02f
                xAxis.axisMinimum = lineChart.xChartMin - 0.02f
            }
            else -> {
                xAxis.labelCount = list.size - 1
                xAxis.axisMaximum = lineChart.xChartMax + 0.1f
                xAxis.axisMinimum = lineChart.xChartMin - 0.1f
            }
        }
//        if(list.size > 1) {
////            xAxis.labelCount = if(list.size == 1) list.size else list.size - 1
//            xAxis.labelCount = list.size - 1
//        } else {
////            xAxis.setLabelCount(list.size, true)
//            xAxis.labelCount = list.size
//        }
//        xAxis.setAvoidFirstLastClipping(true)
        xAxis.valueFormatter = list?.let { MyValueFormatter(it) }
//        xAxis.spaceMin = 0.1f
//        xAxis.spaceMax = 0.1f
        // vertical grid lines
//        xAxis.mAxisRange = list.size.toFloat() - 1f
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.gridColor = Color.rgb(211,214,219)
//        xAxis.axisMaximum = lineChart.xChartMax + 0.1f
//        xAxis.axisMinimum = lineChart.xChartMin - 0.1f

        val yAxis: YAxis = lineChart.axisLeft
        yAxis.gridColor = Color.rgb(211,214,219)
        // disable dual axis (only use LEFT axis)
        lineChart.axisRight.isEnabled = false
        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f)
        // axis range
        yAxis.axisMaximum = upperLimited + 20f
        yAxis.axisMinimum = lowerLimited - 20f

        val llXAxis = LimitLine(9f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 12f
        val ll1 = LimitLine(upperLimited, "최고")
        ll1.lineWidth = 1f
        ll1.lineColor = Color.rgb(46,190,197)
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 12f
        val ll2 = LimitLine(lowerLimited, "최저")
        ll2.lineWidth = 1f
        ll2.lineColor = Color.rgb(46,190,197)
        ll2.enableDashedLine(10f, 10f, 0f)
        ll2.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        ll2.textSize = 12f

        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines
        yAxis.removeAllLimitLines()
        yAxis.addLimitLine(ll1)
        yAxis.addLimitLine(ll2)
        //xAxis.addLimitLine(llXAxis);
        lineChart.setDrawGridBackground(true)
        lineChart.description.isEnabled = false
    }

    private fun setCombChartLayout(list:ArrayList<String>, upperLimited:Float, lowerLimited: Float) {
        val xAxis: XAxis  = combChart.xAxis
//        xAxis.resetAxisMaximum()
//        xAxis.resetAxisMinimum()
//        xAxis.setLabelCount(list.size, true)
//        xAxis.mAxisRange = list.size.toFloat()
//        if(list.size > 1) {
//            xAxis.labelCount = list.size - 1
//        } else {
////            xAxis.setLabelCount(list.size, true)
//            xAxis.labelCount = list.size
//        }
        when(list.size) {
            1 -> {
                xAxis.labelCount = list.size
                xAxis.axisMaximum = combChart.xChartMax + 0.1f
                xAxis.axisMinimum = combChart.xChartMin - 0.1f
            }
            2 -> {
                xAxis.setLabelCount(list.size, true)
                xAxis.axisMaximum = combChart.xChartMax + 0.02f
                xAxis.axisMinimum = combChart.xChartMin - 0.02f
            }
            else -> {
                xAxis.labelCount = list.size - 1
                xAxis.axisMaximum = combChart.xChartMax + 0.1f
                xAxis.axisMinimum = combChart.xChartMin - 0.1f
            }
        }
        xAxis.valueFormatter = MyValueFormatter(list)
        // vertical grid lines
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.gridColor = Color.rgb(211,214,219)

//        xAxis.spaceMin = 0.1f
//        xAxis.spaceMax = 0.1f
//        xAxis.setAvoidFirstLastClipping(true)

        val yAxis: YAxis = combChart.axisLeft
        yAxis.gridColor = Color.rgb(211,214,219)
        yAxis.axisMinimum = 0f
        // disable dual axis (only use LEFT axis)
        combChart.axisRight.isEnabled = false
        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f)
        // axis range
        yAxis.axisMaximum = upperLimited + 80f
        yAxis.axisMinimum = lowerLimited - 50f

        val l: Legend = combChart.legend
        l.formSize = 15f
        l.textSize = 15f
        l.isWordWrapEnabled = false
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
//        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(true)
        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines
        yAxis.removeAllLimitLines()
        combChart.setDrawGridBackground(true)
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
        set.setValueTextSize(15f)
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
        var d = ScatterData()
        appDatabase = AppDatabase.getInstance(this)
        val xValsDateLabel = ArrayList<String>()
        val calendar = GregorianCalendar.getInstance()
        var upperLimited: Float = 0f
        var lowerLimited: Float = 0f
        val values0 = arrayListOf<Entry>()
        val values1 = arrayListOf<Entry>()
        var timeLine = appDatabase?.graphDao()?.timeLineData(curDate)?: return null

        timeLineOfDay = timeLine

        for ((i, day: GraphTimeLine) in timeLine?.withIndex()) {
            if(i == 0) {
                upperLimited = day.totalCapacity.toString().toFloat()
                lowerLimited = day.totalCapacity.toString().toFloat()
            }

            if(day.totalCapacity.toString().toFloat() > upperLimited) {
                upperLimited = day.totalCapacity.toString().toFloat()
            }

            if(day.totalCapacity.toString().toFloat() < lowerLimited) {
                lowerLimited = day.totalCapacity.toString().toFloat()
            }

            when(day.dataType) {
                0 -> {
                    values0.add(Entry(i.toFloat(), day.totalCapacity.toString().toFloat()))
                }

                1 ->
                {
                    values1.add(Entry(i.toFloat(), day.totalCapacity.toString().toFloat()))
                }
            }

            calendar.timeInMillis = day.millis
            xValsDateLabel.add(
                "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(
                    Calendar.MINUTE
                )}"
            )
        }

        setCombChartLayout(xValsDateLabel, upperLimited, lowerLimited)

        // create a dataset and give it a type
        val set1 = ScatterDataSet(values0, "사료")
        set1.setColors(Color.rgb(245,161,27))
        set1.setValueTextColors(arrayListOf(Color.DKGRAY))
        set1.setScatterShape(ScatterChart.ScatterShape.SQUARE)
        set1. scatterShapeSize = 15f
        set1.setDrawValues(true)
//        set1.setDrawIcons(true)
        set1.valueTextSize = 15f
//        set1.scatterShapeHoleRadius = 4f

        val set2 = ScatterDataSet(values1, "인슐린")
        set2.setColors(Color.rgb(236,32,8))
        set2.setValueTextColors(arrayListOf(Color.DKGRAY))
        set2.scatterShapeSize = 15f
        set2.setDrawValues(true)
//        set1.setDrawIcons(true)
        set2.valueTextSize = 15f
//        set2.scatterShapeHoleRadius = 4f

        d.addDataSet(set1)
        d.addDataSet(set2)

        return d
    }

    private fun resetChart() {
        lineChart.xAxis.resetAxisMaximum()
        lineChart.xAxis.resetAxisMinimum()
        combChart.xAxis.resetAxisMaximum()
        combChart.xAxis.resetAxisMinimum()
    }

    private fun setButtonEvent() {
        btnNext.setOnClickListener {
            var day = ""
            try {
                day = daysOfMonthMap[indexOfMonth][--indexOfDay]
            } catch (e: Exception) {
                var prevDayOfMon = ""
                try {
                    prevDayOfMon = daysOfMonthMap[--indexOfMonth][0]
                    indexOfDay = 0
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        getString(R.string.com_j2d2_graph_message_last),
                        Toast.LENGTH_SHORT
                    ).show()
                    indexOfMonth++
                    indexOfDay++
//                    indexOfDay++
                    return@setOnClickListener
                }

                day = prevDayOfMon
            }

            if(!day.isNullOrBlank()) {
                resetChart()
                textInfo.text = ""
                setDate(day)
                CoroutineScope(Dispatchers.IO).launch {
                    loadGlaucoseData(day)
                    loadCombinedData(day)
                }
            }
        }

        btnPrev.setOnClickListener {
            var day = ""
            try {
                day = daysOfMonthMap[indexOfMonth][++indexOfDay]
            } catch (e: IndexOutOfBoundsException) {
                var prevDayOfMon = ""

                try {
                    prevDayOfMon = daysOfMonthMap[++indexOfMonth][0]
                    indexOfDay = 0
                } catch (e: IndexOutOfBoundsException) {
                    Toast.makeText(
                        this,
                        getString(R.string.com_j2d2_graph_message_last),
                        Toast.LENGTH_SHORT
                    ).show()
                    indexOfDay--
                    indexOfMonth--
//                    indexOfDay--
                    return@setOnClickListener
                }
                day = prevDayOfMon
            }

            if(!day.isNullOrBlank()) {
                resetChart()
                textInfo.text = ""
                setDate(day)
                CoroutineScope(Dispatchers.IO).launch {
                    loadGlaucoseData(day)
                    loadCombinedData(day)
                }
            }
        }

        btnModify.setOnClickListener {
            if(selectedDataType == DataType.NONE) {
                Toast.makeText(
                    this,
                    getString(R.string.delete_alert_message),
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            var intent: Intent? = null

            when(selectedDataType) {
                DataType.FEEDING -> {
                    intent = Intent(this, FeedingActivity::class.java)
                }
                DataType.INSULIN -> {
                    intent = Intent(this, InsulinActivity::class.java)
                }
                DataType.BLOODSUGAR -> {
                    intent = Intent(this, BloodGlucoseActivity::class.java)
                }
            }

            intent?.putExtra("data", selectedParcel)
            textInfo.text = ""
            startActivityForResult(intent, 100)
        }

        btnDelete.setOnClickListener {
            if(selectedDataType == DataType.NONE) {
                Toast.makeText(
                    this,
                    getString(R.string.delete_alert_message),
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val dlg = PopupDialog(this, this)
            dlg.start(getString(R.string.delete_message))
        }
    }

    class MyValueFormatter(private val xValsDateLabel: ArrayList<String>) : ValueFormatter() {
//        companion object {
//
//        }

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
//        Log.i(
//            "VAL SELECTED",
//            "Value: " + e!!.y + ", xIndex: " + e!!.x
//                    + ", DataSet index: " + h!!.dataSetIndex
//        )
        val gloucose = gloucoseListOfDay[e!!.x.toInt()]
        val calendar = GregorianCalendar.getInstance()
        calendar.timeInMillis = gloucose.millis
        selectedDataType = DataType.BLOODSUGAR
        selectedParcel = BloodGlucoseParcel(gloucose.millis, gloucose.dataType, gloucose.bloodSugar)
        textInfo.text = "시간 : %02d시%02d분\n혈당 : %d".format(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), gloucose.bloodSugar)
    }

    class MyScatterChartTouchListener(private val timeLineOfDay: List<GraphTimeLine>,
                                      private val textInfo:TextView,
                                      private var appDatabase: AppDatabase?,
                                      private val callBack: OnSelectedDataCallBack) : OnChartValueSelectedListener {
        override fun onNothingSelected() {}

        override fun onValueSelected(e: Entry?, h: Highlight?) {
            val timeline = timeLineOfDay[e!!.x.toInt()]
            val calendar = GregorianCalendar.getInstance()
            calendar.timeInMillis = timeline.millis

            when(timeline.dataType) {
                0 -> {
                    var parcel:FeedParcel
                    CoroutineScope(Dispatchers.IO).launch {
                        val data = appDatabase?.feedingDao()?.findByTodyWithMillis(timeline.millis)!!
                        CoroutineScope(Dispatchers.Main).launch {
                            textInfo.text =
                                "시간 : %02d시%02d분\n브랜드: %s\n사료량 : %dg\n비고 : %s".format(
                                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                                data.brandName,
                                data.totalCapacity,
                                data.remark)
                        }
                        parcel = FeedParcel(data.millis, data.dataType,data.type,data.brandName,data.totalCapacity,data.remark)
                        callBack.setDataType(DataType.FEEDING, parcel = parcel)
                    }
                }

                1 -> {
                    var parcel:InsulinParcel
                    CoroutineScope(Dispatchers.IO).launch {
                        val data =
                            appDatabase?.insulinDao()?.findByTodyWithMillis(timeline.millis)!!
                        CoroutineScope(Dispatchers.Main).launch {
                            textInfo.text =
                                "시간 : %02d시%02d분\n종류 : %s\n원액양 : %1.1f iu\n주사량 : %d iu\n희석 : %s\n비고 : %s".format(
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    if (data.type == 0) "휴물린엔" else "캐닌슐린",
                                    data.undiluted,
                                    data.totalCapacity,
                                    if (data.dilution == 1) "Yes" else "No",
                                    data.remark
                                )
                            parcel = InsulinParcel(data.millis, data.dataType,data.type,data.undiluted,data.totalCapacity,data.dilution,data.remark)
                            callBack.setDataType(DataType.INSULIN, parcel = parcel)
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    loadData()
                }
            }
        }
    }

    override fun setDataType(type: DataType, parcel:Terry) {
        selectedDataType = type
        selectedParcel = parcel
    }

    override fun OnPositiveClick() {
        var parcel: Terry

        when(selectedDataType) {
            DataType.FEEDING -> {
                parcel = selectedParcel as FeedParcel
                val data = Feeding(parcel.millis, parcel.dataType,parcel.type,parcel.brandName,parcel.totalCapacity,parcel.remark)
                CoroutineScope(Dispatchers.IO).launch {
                    appDatabase?.feedingDao()?.delete(data)
                }
            }
            DataType.INSULIN -> {
                parcel = selectedParcel as InsulinParcel
                val data = Insulin(parcel.millis, parcel.dataType,parcel.type,parcel.undiluted,parcel.totalCapacity,parcel.dilution,parcel.remark)
                CoroutineScope(Dispatchers.IO).launch {
                    appDatabase?.insulinDao()?.delete(data)
                }
            }
            DataType.BLOODSUGAR -> {
                parcel = selectedParcel as BloodGlucoseParcel
                val data = BloodGlucose(parcel.millis, parcel.dataType, parcel.bloodSugar)
                CoroutineScope(Dispatchers.IO).launch {
                    appDatabase?.bloodGlucoseDao()?.delete(data)
                }
            }
        }
        selectedDataType = DataType.NONE
        textInfo.text = ""

        CoroutineScope(Dispatchers.Main).launch {
            lineChart.notifyDataSetChanged()
            lineChart.data.notifyDataChanged()
            combChart.notifyDataSetChanged()
            combChart.data.notifyDataChanged()
            resetChart()
            loadData()
        }
    }

    override fun OnNegativeClick() {

    }
}