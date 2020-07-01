package com.j2d2.graph

import androidx.room.Dao
import androidx.room.Query

@Dao
interface GraphDao {

    @Query("SELECT data_type, millis, total_capacity FROM insulin WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') LIKE :today\n" +
            "UNION\n" +
            "SELECT data_type, millis, total_capacity FROM feeding WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') LIKE :today\n" +
            "ORDER BY millis")
    fun timeLineData(today:String):List<GraphTimeLine>

    @Query("")
    fun getLatestDay():Long

    @Query("SELECT date(date(millis/1000,'unixepoch','localtime'), '-1 month') as DT FROM feeding WHERE strftime(\"%Y%m\",date(date(millis/1000,'unixepoch','localtime'), '-1 month'),'localtime') LIKE :month GROUP BY DT ORDER BY DT DESC")
    fun getDayListOfMonth(month:String):List<String>

//    @Query("SELECT date(date(millis/1000,'unixepoch','localtime'), '-1 month') as DT FROM feeding WHERE strftime('%Y-%m', DT) LIKE :month GROUP BY DT ORDER BY DT DESC")
//    fun dayListOfMonth(month:String):String

}