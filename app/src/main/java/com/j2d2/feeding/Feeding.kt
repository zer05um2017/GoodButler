package com.j2d2.feeding

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feeding")
data class Feeding(
    @PrimaryKey val millis: Long,
    @ColumnInfo(name = "data_type") val dataType: Int,         // 데이터 타입 0:사료, 1:인슐린, 2:혈당
    @ColumnInfo(name = "feed_type") val type: Int,                 // 0:건식, 1:습식
    @ColumnInfo(name = "brand_name") val brandName: String?,         // 사료 브랜드
    @ColumnInfo(name = "total_capacity") val totalCapacity: Int,   // 총 급여량
    @ColumnInfo(name = "remark") val remark: String?                // 메모
)