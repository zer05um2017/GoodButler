package com.j2d2.feeding

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feeding")
data class Feeding(
    @PrimaryKey(autoGenerate = true) val uid: Long,
    @ColumnInfo(name = "millis") val millis: Long,
    @ColumnInfo(name = "feed_type") val type: Int?,                 // 0:건식, 1:습식
    @ColumnInfo(name = "brand_name") val brandName: String?,         // 사료 브랜드
    @ColumnInfo(name = "feeding_amount") val feedingAmount: Int?,   // 총 급여량
    @ColumnInfo(name = "remark") val remark: String?                // 메모
)