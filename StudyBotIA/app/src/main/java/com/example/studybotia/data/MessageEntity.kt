package com.tuapp.studybotia.data

data class MessageEntity(

    val id: Int = 0,

    val text: String,

    val isUser: Boolean,

    val time: Long = System.currentTimeMillis()
)