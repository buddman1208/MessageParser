package com.example.messageparser

import java.io.Serializable

data class PostModel(
    var sender: String,
    var received: String,
    var message: String,
    var key: String
) : Serializable