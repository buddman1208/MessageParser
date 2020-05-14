package com.example.messageparser.db

import androidx.room.*

@Dao
interface FailureDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFailures(vararg failures : Failure)

    @Delete
    fun deleteFailures(vararg failures : Failure)

    @Query("SELECT * FROM Failures")
    fun loadAllFailures() : List<Failure>

    @Update
    fun updateFailures(vararg failures : Failure)
}