package com.j2d2.bloodglucose

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bloodglucose")
data class BloodGlucose (
    @PrimaryKey val millis: Long, // milli seconds
    @ColumnInfo(name = "data_type") val dataType: Int,       // 데이터 타입 0:사료, 1:인슐린, 2:혈당
    @ColumnInfo(name = "blood_glucose") val bloodSugar: Int, // 혈당
    @ColumnInfo(name = "pet_id") val petId:Long              // 강아지 ID
)