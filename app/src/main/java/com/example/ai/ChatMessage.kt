package com.example.ai

data class ChatMessage(
    val sender: String,
    val text: String,
    val isThinkingModel: Boolean = false,
    val timestamp: String = "JUST NOW"
)
