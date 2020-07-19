package com.j2d2.petinfo

import androidx.room.*
import com.j2d2.feeding.Feeding

@Dao
interface BreedDao {
    @Query("SELECT * FROM breed ORDER BY name")
    fun getBreedList(): List<Breed>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(breedList: List<Breed>)
}