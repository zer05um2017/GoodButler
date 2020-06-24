package com.j2d2.graph

import com.github.mikephil.charting.charts.LineChart

interface InvalidateData {
    fun invalidate(lineChart: LineChart)
}