package com.j2d2.graph

import androidx.room.ColumnInfo

class GraphTimeLine (
    @ColumnInfo(name = "data_type") val dataType: Int?,             // 데이터 타입 0:사료, 1:인슐린, 2:혈당
    @ColumnInfo(name = "millis") val millis: Long,                  // milli seconds
    @ColumnInfo(name = "total_capacity") val totalCapacity: Int?    // amount
)