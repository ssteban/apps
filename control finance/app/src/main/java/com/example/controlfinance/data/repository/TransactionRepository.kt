package com.example.controlfinance.data.repository

import android.content.ContentValues
import com.example.controlfinance.data.local.FinanceDatabaseHelper
import com.example.controlfinance.data.local.FinanceDbContract
import com.example.controlfinance.data.model.BalanceSummary
import com.example.controlfinance.data.model.CategoryMonthlyTotal
import com.example.controlfinance.data.model.Transaction
import com.example.controlfinance.data.model.TransactionType

class TransactionRepository(
    private val dbHelper: FinanceDatabaseHelper
) {

    fun create(transaction: Transaction): Long {
        require(transaction.amount > 0) { "Amount must be greater than zero" }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FinanceDbContract.Transactions.AMOUNT, transaction.amount)
            put(FinanceDbContract.Transactions.TYPE, transaction.type.value)
            put(FinanceDbContract.Transactions.DESCRIPTION, transaction.description)
            put(FinanceDbContract.Transactions.CATEGORY_ID, transaction.categoryId)
            put(
                FinanceDbContract.Transactions.TRANSACTION_DATE,
                if (transaction.transactionDate > 0) transaction.transactionDate else System.currentTimeMillis()
            )
            put(
                FinanceDbContract.Transactions.CREATED_AT,
                if (transaction.createdAt > 0) transaction.createdAt else System.currentTimeMillis()
            )
        }
        return db.insertOrThrow(FinanceDbContract.Transactions.TABLE, null, values)
    }

    fun getById(id: Long): Transaction? {
        val db = dbHelper.readableDatabase
        db.query(
            FinanceDbContract.Transactions.TABLE,
            null,
            "${FinanceDbContract.Transactions.ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (!cursor.moveToFirst()) return null
            return cursorToTransaction(cursor)
        }
    }

    fun update(transaction: Transaction): Boolean {
        require(transaction.id > 0) { "Transaction id must be greater than zero" }
        require(transaction.amount > 0) { "Amount must be greater than zero" }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FinanceDbContract.Transactions.AMOUNT, transaction.amount)
            put(FinanceDbContract.Transactions.TYPE, transaction.type.value)
            put(FinanceDbContract.Transactions.DESCRIPTION, transaction.description)
            put(FinanceDbContract.Transactions.CATEGORY_ID, transaction.categoryId)
            put(FinanceDbContract.Transactions.TRANSACTION_DATE, transaction.transactionDate)
        }
        val rows = db.update(
            FinanceDbContract.Transactions.TABLE,
            values,
            "${FinanceDbContract.Transactions.ID} = ?",
            arrayOf(transaction.id.toString())
        )
        return rows > 0
    }

    fun delete(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        val rows = db.delete(
            FinanceDbContract.Transactions.TABLE,
            "${FinanceDbContract.Transactions.ID} = ?",
            arrayOf(id.toString())
        )
        return rows > 0
    }

    fun listByDateRange(startDateMillis: Long, endDateMillis: Long): List<Transaction> {
        val db = dbHelper.readableDatabase
        val result = mutableListOf<Transaction>()
        db.query(
            FinanceDbContract.Transactions.TABLE,
            null,
            "${FinanceDbContract.Transactions.TRANSACTION_DATE} BETWEEN ? AND ?",
            arrayOf(startDateMillis.toString(), endDateMillis.toString()),
            null,
            null,
            "${FinanceDbContract.Transactions.TRANSACTION_DATE} DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(cursorToTransaction(cursor))
            }
        }
        return result
    }

    fun getBalanceSummary(): BalanceSummary {
        val db = dbHelper.readableDatabase
        val income = sumByType(db, TransactionType.INCOME)
        val expense = sumByType(db, TransactionType.EXPENSE)
        return BalanceSummary(totalIncome = income, totalExpense = expense)
    }

    fun getMonthlyTotalsByCategory(startDateMillis: Long, endDateMillis: Long): List<CategoryMonthlyTotal> {
        val db = dbHelper.readableDatabase
        val sql = """
            SELECT
                c.${FinanceDbContract.Categories.ID} AS category_id,
                c.${FinanceDbContract.Categories.NAME} AS category_name,
                c.${FinanceDbContract.Categories.TYPE} AS category_type,
                SUM(t.${FinanceDbContract.Transactions.AMOUNT}) AS total_amount
            FROM ${FinanceDbContract.Transactions.TABLE} t
            INNER JOIN ${FinanceDbContract.Categories.TABLE} c
                ON c.${FinanceDbContract.Categories.ID} = t.${FinanceDbContract.Transactions.CATEGORY_ID}
            WHERE t.${FinanceDbContract.Transactions.TRANSACTION_DATE} BETWEEN ? AND ?
            GROUP BY c.${FinanceDbContract.Categories.ID}, c.${FinanceDbContract.Categories.NAME}, c.${FinanceDbContract.Categories.TYPE}
            ORDER BY total_amount DESC
        """.trimIndent()

        val result = mutableListOf<CategoryMonthlyTotal>()
        db.rawQuery(sql, arrayOf(startDateMillis.toString(), endDateMillis.toString())).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    CategoryMonthlyTotal(
                        categoryId = cursor.getLong(cursor.getColumnIndexOrThrow("category_id")),
                        categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name")),
                        type = TransactionType.fromValue(
                            cursor.getString(cursor.getColumnIndexOrThrow("category_type"))
                        ),
                        totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"))
                    )
                )
            }
        }
        return result
    }

    private fun sumByType(db: android.database.sqlite.SQLiteDatabase, type: TransactionType): Double {
        val sql = """
            SELECT COALESCE(SUM(${FinanceDbContract.Transactions.AMOUNT}), 0)
            FROM ${FinanceDbContract.Transactions.TABLE}
            WHERE ${FinanceDbContract.Transactions.TYPE} = ?
        """.trimIndent()

        db.rawQuery(sql, arrayOf(type.value)).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0
        }
    }

    private fun cursorToTransaction(cursor: android.database.Cursor): Transaction {
        return Transaction(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(FinanceDbContract.Transactions.ID)),
            amount = cursor.getDouble(cursor.getColumnIndexOrThrow(FinanceDbContract.Transactions.AMOUNT)),
            type = TransactionType.fromValue(
                cursor.getString(cursor.getColumnIndexOrThrow(FinanceDbContract.Transactions.TYPE))
            ),
            description = cursor.getString(cursor.getColumnIndexOrThrow(FinanceDbContract.Transactions.DESCRIPTION)),
            categoryId = cursor.getLong(cursor.getColumnIndexOrThrow(FinanceDbContract.Transactions.CATEGORY_ID)),
            transactionDate = cursor.getLong(
                cursor.getColumnIndexOrThrow(FinanceDbContract.Transactions.TRANSACTION_DATE)
            ),
            createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(FinanceDbContract.Transactions.CREATED_AT))
        )
    }
}
