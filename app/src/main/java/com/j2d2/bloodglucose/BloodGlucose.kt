package com.j2d2.bloodglucose

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bloodglucose")
data class BloodGlucose (
    @PrimaryKey(autoGenerate = true) val uid: Long,
    @ColumnInfo(name = "millis") val millis: Long, // milli seconds
    @ColumnInfo(name = "blood_glucose") val bloodSugar: Int? // 혈당
)