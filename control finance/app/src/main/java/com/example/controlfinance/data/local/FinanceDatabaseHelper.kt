package com.example.controlfinance.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FinanceDatabaseHelper(context: Context) :
    SQLiteOpenHelper(
        context,
        FinanceDbContract.DATABASE_NAME,
        null,
        FinanceDbContract.DATABASE_VERSION
    ) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createCategoriesTableSql())
        db.execSQL(createTransactionsTableSql())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${FinanceDbContract.Transactions.TABLE}")
        db.execSQL("DROP TABLE IF EXISTS ${FinanceDbContract.Categories.TABLE}")
        onCreate(db)
    }

    private fun createCategoriesTableSql(): String {
        return """
            CREATE TABLE ${FinanceDbContract.Categories.TABLE} (
                ${FinanceDbContract.Categories.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${FinanceDbContract.Categories.NAME} TEXT NOT NULL,
                ${FinanceDbContract.Categories.TYPE} TEXT NOT NULL CHECK(${FinanceDbContract.Categories.TYPE} IN ('income','expense')),
                ${FinanceDbContract.Categories.CREATED_AT} INTEGER NOT NULL
            )
        """.trimIndent()
    }

    private fun createTransactionsTableSql(): String {
        return """
            CREATE TABLE ${FinanceDbContract.Transactions.TABLE} (
                ${FinanceDbContract.Transactions.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${FinanceDbContract.Transactions.AMOUNT} REAL NOT NULL CHECK(${FinanceDbContract.Transactions.AMOUNT} > 0),
                ${FinanceDbContract.Transactions.TYPE} TEXT NOT NULL CHECK(${FinanceDbContract.Transactions.TYPE} IN ('income','expense')),
                ${FinanceDbContract.Transactions.DESCRIPTION} TEXT,
                ${FinanceDbContract.Transactions.CATEGORY_ID} INTEGER NOT NULL,
                ${FinanceDbContract.Transactions.TRANSACTION_DATE} INTEGER NOT NULL,
                ${FinanceDbContract.Transactions.CREATED_AT} INTEGER NOT NULL,
                FOREIGN KEY(${FinanceDbContract.Transactions.CATEGORY_ID})
                    REFERENCES ${FinanceDbContract.Categories.TABLE}(${FinanceDbContract.Categories.ID})
                    ON DELETE RESTRICT
            )
        """.trimIndent()
    }
}
