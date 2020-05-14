package com.example.messageparser.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Failure::class], version = 1, exportSchema = false)
abstract class FailureDB: RoomDatabase() {
    abstract fun failureDao(): FailureDAO

    companion object {
        private var INSTANCE: FailureDB? = null

        fun getInstance(context: Context): FailureDB? {
            if (INSTANCE == null) {
                synchronized(FailureDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            FailureDB::class.java, "failure.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}