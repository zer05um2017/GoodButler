package com.j2d2.insulin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insulin")
data class Insulin (
    @PrimaryKey(autoGenerate = true) val uid: Long,
    @ColumnInfo(name = "millis") val millis: Long,
    @ColumnInfo(name = "insulin_type") val type: Int?,              // 0:휴물린엔, 1:캐닌슐린
    @ColumnInfo(name = "undiluted_vol") val undiluted: Float?,      // 원액량
    @ColumnInfo(name = "total_capacity") val totalCapacity: Int?, // 총 주사량(희석일 경우 총량)
    @ColumnInfo(name = "dilution") val dilution: Int?,              // 희석:1, 아님:0
    @ColumnInfo(name = "remark") val remark: String?                // 메모
)