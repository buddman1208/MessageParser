package com.example.messageparser.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Sets")
data class Set(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var title: String,
    var from: String,
    var sendUrl: String,
    var key: String
) : Serializable