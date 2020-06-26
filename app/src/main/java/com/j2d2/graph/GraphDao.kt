package com.j2d2.graph

import androidx.room.Dao
import androidx.room.Query

@Dao
interface GraphDao {

    @Query("SELECT millis FROM insulin WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') LIKE :today\n" +
            "UNION\n" +
            "SELECT millis FROM feeding WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') LIKE :today\n" +
            "ORDER BY millis")
    fun timeLineData(today:String):List<Long>

}