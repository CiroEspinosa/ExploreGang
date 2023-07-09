package com.example.exploregang.data.model

import java.util.*

data class Message(
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val messageText: String,
    val timestamp: Long
)