package com.example.controlfinance.data.repository

import android.content.ContentValues
import com.example.controlfinance.data.local.FinanceDatabaseHelper
import com.example.controlfinance.data.local.FinanceDbContract
import com.example.controlfinance.data.model.Category
import com.example.controlfinance.data.model.TransactionType

class CategoryRepository(
    private val dbHelper: FinanceDatabaseHelper
) {

    fun create(category: Category): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FinanceDbContract.Categories.NAME, category.name.trim())
            put(FinanceDbContract.Categories.TYPE, category.type.value)
            put(FinanceDbContract.Categories.CREATED_AT, category.createdAt)
        }
        return db.insertOrThrow(FinanceDbContract.Categories.TABLE, null, values)
    }

    fun getById(id: Long): Category? {
        val db = dbHelper.readableDatabase
        db.query(
            FinanceDbContract.Categories.TABLE,
            null,
            "${FinanceDbContract.Categories.ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (!cursor.moveToFirst()) return null
            return cursorToCategory(cursor)
        }
    }

    fun listByType(type: TransactionType): List<Category> {
        val db = dbHelper.readableDatabase
        val result = mutableListOf<Category>()
        db.query(
            FinanceDbContract.Categories.TABLE,
            null,
            "${FinanceDbContract.Categories.TYPE} = ?",
            arrayOf(type.value),
            null,
            null,
            "${FinanceDbContract.Categories.NAME} ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(cursorToCategory(cursor))
            }
        }
        return result
    }

    fun update(category: Category): Boolean {
        require(category.id > 0) { "Category id must be greater than zero" }
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FinanceDbContract.Categories.NAME, category.name.trim())
            put(FinanceDbContract.Categories.TYPE, category.type.value)
        }
        val rows = db.update(
            FinanceDbContract.Categories.TABLE,
            values,
            "${FinanceDbContract.Categories.ID} = ?",
            arrayOf(category.id.toString())
        )
        return rows > 0
    }

    fun delete(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        val rows = db.delete(
            FinanceDbContract.Categories.TABLE,
            "${FinanceDbContract.Categories.ID} = ?",
            arrayOf(id.toString())
        )
        return rows > 0
    }

    private fun cursorToCategory(cursor: android.database.Cursor): Category {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(FinanceDbContract.Categories.ID))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(FinanceDbContract.Categories.NAME))
        val type = cursor.getString(cursor.getColumnIndexOrThrow(FinanceDbContract.Categories.TYPE))
        val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(FinanceDbContract.Categories.CREATED_AT))

        return Category(
            id = id,
            name = name,
            type = TransactionType.fromValue(type),
            createdAt = createdAt
        )
    }
}
