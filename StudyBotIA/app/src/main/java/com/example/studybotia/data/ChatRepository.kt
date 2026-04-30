package com.tuapp.studybotia.data

class ChatRepository(
    private val dao: MessageDao
) {

    fun saveMessage(text: String, isUser: Boolean) {
        dao.insertMessage(
            MessageEntity(
                text = text,
                isUser = isUser
            )
        )
    }

    fun getMessages(): List<MessageEntity> {
        return dao.getAllMessages()
    }

    fun clearChat() {
        dao.deleteAll()
    }
}