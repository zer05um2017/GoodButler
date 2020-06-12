package com.j2d2.insulin
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface InsulinDao {
    @Query("SELECT * FROM insulin")
    fun getAll(): List<Insulin>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Insulin>

    @Query("SELECT * FROM insulin WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Insulin

    @Insert
    fun insert(vararg insulin: Insulin)

//    @Insert
//    fun insertAll(vararg users: Insulin)

    @Query("DELETE FROM insulin")
    fun deleteAll()

    @Delete
    fun delete(insulin: Insulin)
}