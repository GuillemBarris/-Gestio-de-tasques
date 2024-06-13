package com.example.gestiodetasques

import android.content.Context

object DatabaseHelper {
    private var database: Database? = null

    fun getInstance(context: Context): Database {
        if (database == null) {
            database = Database(context.applicationContext)
        }
        return database!!
    }
}