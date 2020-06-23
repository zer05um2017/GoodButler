package com.j2d2.insulin

import androidx.room.*

@Dao
interface InsulinDao {
//    @Query("SELECT * FROM insulin")
//    fun getAll(): List<Insulin>

//    @Query("SELECT * FROM insulin WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<Insulin>

    @Query("SELECT * FROM insulin WHERE date(date(millis/1000,'unixepoch','localtime'), '-1 month') LIKE :today ORDER BY millis")
    fun findByToday(today: String): List<Insulin>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg insulin: Insulin)

//    @Insert
//    fun insertAll(vararg users: Insulin)

    @Query("DELETE FROM insulin")
    fun deleteAll()

    @Delete
    fun delete(insulin: Insulin)

    /**
     *     @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
    "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insert(vararg user: User)

    @Insert
    fun insertAll(vararg users: User)

    @Query("DELETE FROM user")
    fun deleteAll()

    @Delete
    fun delete(user: User)
     */
}