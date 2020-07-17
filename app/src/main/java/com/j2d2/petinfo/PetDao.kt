package com.j2d2.petinfo
import androidx.room.*

@Dao
interface PetDao {
    @Query("SELECT * FROM pet WHERE id LIKE:id ORDER BY id")
    fun findPetById(id:Long): Pet

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(pet: Pet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pet: Pet)
}