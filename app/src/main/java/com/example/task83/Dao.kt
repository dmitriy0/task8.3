package com.example.task83

import androidx.room.*
import androidx.room.Dao
import androidx.room.OnConflictStrategy.REPLACE


@Dao
interface Dao {

    @Query("SELECT * FROM EntityImages")
    fun getAll(): List<EntityImages?>?

    @Query("SELECT * from EntityImages WHERE id = :id")
    fun getItemById(id: Int): List<EntityImages?>?

    @Insert(onConflict = REPLACE)
    fun insert(entityImages: EntityImages?)

    @Query("DELETE FROM EntityImages")
    fun clearTable()

}