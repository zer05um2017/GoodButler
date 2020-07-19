package com.j2d2.pedometer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedometer")
data class Pedometer (
    @PrimaryKey val millis: Long,
    @ColumnInfo(name = "step_count") val stepCount: Long,
    @ColumnInfo(name = "petId") val petId: Long
)