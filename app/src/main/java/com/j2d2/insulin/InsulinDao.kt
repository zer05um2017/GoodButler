package com.j2d2.insulin

import androidx.room.*

@Dao
interface InsulinDao {
//    @Query("SELECT * FROM insulin")
//    fun getAll(): List<Insulin>

//    @Query("SELECT * FROM insulin WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Insulin>

    @Query("SELECT * FROM insulin WHERE date LIKE :today ORDER BY date, time DESC")
    fun findByToday(today: String): List<Insulin>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg insulin: Insulin)

//    @Insert
//    fun insertAll(vararg users: Insulin)

    @Query("DELETE FROM insulin")
    fun deleteAll()

    @Delete
    fun delete(insulin: Insulin)
}