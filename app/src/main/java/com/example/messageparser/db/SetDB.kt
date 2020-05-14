package com.example.messageparser.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Set::class], version = 1, exportSchema = false)
abstract class SetDB: RoomDatabase() {
    abstract fun setDao(): SetDAO

    companion object {
        private var INSTANCE: SetDB? = null

        fun getInstance(context: Context): SetDB? {
            if (INSTANCE == null) {
                synchronized(SetDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            SetDB::class.java, "set.db")
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