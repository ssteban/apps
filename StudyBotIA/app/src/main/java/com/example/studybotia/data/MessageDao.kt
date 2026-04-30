package com.tuapp.studybotia.data

import android.content.ContentValues

class MessageDao(
    private val dbHelper: AppDatabase
) {

    fun insertMessage(message: MessageEntity) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("text", message.text)
            put("isUser", if (message.isUser) 1 else 0)
            put("time", message.time)
        }

        db.insert("messages", null, values)
    }

    fun getAllMessages(): List<MessageEntity> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<MessageEntity>()

        val cursor = db.rawQuery(
            "SELECT * FROM messages ORDER BY time ASC",
            null
        )

        while (cursor.moveToNext()) {
            list.add(
                MessageEntity(
                    id = cursor.getInt(0),
                    text = cursor.getString(1),
                    isUser = cursor.getInt(2) == 1,
                    time = cursor.getLong(3)
                )
            )
        }

        cursor.close()
        return list
    }

    fun deleteAll() {
        val db = dbHelper.writableDatabase
        db.execSQL("DELETE FROM messages")
    }
}