package com.j2d2.insulin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insulin")
data class Insulin (
    @PrimaryKey val millis: Long,
    @ColumnInfo(name = "data_type") val dataType: Int,         // 데이터 타입 0:사료, 1:인슐린, 2:혈당
    @ColumnInfo(name = "insulin_type") val type: Int,              // 0:휴물린엔, 1:캐닌슐린
    @ColumnInfo(name = "undiluted_vol") val undiluted: Float,      // 원액량
    @ColumnInfo(name = "total_capacity") val totalCapacity: Int, // 총 주사량(희석일 경우 총량)
    @ColumnInfo(name = "dilution") val dilution: Int,              // 희석:1, 아님:0
    @ColumnInfo(name = "remark") val remark: String?,                // 메모
    @ColumnInfo(name = "pet_id") val petId:Long              // 강아지 ID
)