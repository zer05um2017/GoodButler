package com.j2d2.petinfo
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breed")
data class Breed (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int,      // ID
    @ColumnInfo(name = "type") val type:String  // Type
)