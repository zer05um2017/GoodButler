package com.j2d2.pedometer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface PedometerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg pedometer: Pedometer)
}