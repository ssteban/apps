package com.example.controlfinance.data.local

object FinanceDbContract {
    const val DATABASE_NAME = "control_finance.db"
    const val DATABASE_VERSION = 1

    object Categories {
        const val TABLE = "categories"
        const val ID = "id"
        const val NAME = "name"
        const val TYPE = "type"
        const val CREATED_AT = "created_at"
    }

    object Transactions {
        const val TABLE = "transactions"
        const val ID = "id"
        const val AMOUNT = "amount"
        const val TYPE = "type"
        const val DESCRIPTION = "description"
        const val CATEGORY_ID = "category_id"
        const val TRANSACTION_DATE = "transaction_date"
        const val CREATED_AT = "created_at"
    }
}
