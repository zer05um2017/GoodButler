package com.j2d2.graph

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.j2d2.R
import kotlinx.android.synthetic.main.activity_graph.*

class GraphActivity : AppCompatActivity() {
    private var data: ArrayList<Entry>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        if(savedInstanceState != null) {
            this.data = savedInstanceState.getSerializable("chart") as ArrayList<Entry>?
        } else {
            this.data = generateDataLine(1)
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
        val values1 = arrayListOf<Entry>()

        for(i:Int in 1..12) {
            values1.add(Entry(i.toFloat(), ((Math.random() * 65) + 40).toFloat()))
        }
        this.data = values1
        return values1
    }
}