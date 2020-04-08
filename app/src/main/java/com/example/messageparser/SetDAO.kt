package com.example.messageparser

import androidx.room.*

@Dao
interface SetDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSets(vararg sets : Set)

    @Delete
    fun deleteSets(vararg sets : Set)

    @Query("SELECT * FROM sets")
    fun loadAllSets() : List<Set>

    @Query("SELECT * FROM sets WHERE `from` like :from")
    fun searchNumber(from : String) : List<Set>

    @Update
    fun updateSets(vararg sets : Set)
}