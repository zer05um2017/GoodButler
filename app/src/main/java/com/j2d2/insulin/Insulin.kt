package com.j2d2.insulin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Insulin (
    @PrimaryKey(autoGenerate = true) val uid: Long,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "time") val lastName: String?
)