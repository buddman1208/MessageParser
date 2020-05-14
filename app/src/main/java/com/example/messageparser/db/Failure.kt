package com.example.messageparser.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Failures")
data class Failure(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var sender: String,
    var received: String,
    var message: String,
    var key: String,
    var createDate: Long,
    var url: String
) : Serializable