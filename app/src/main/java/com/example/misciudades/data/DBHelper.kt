// DBHelper.kt
package com.example.misciudades.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "mis_ciudades.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_CAPITALES = "capitales"
        const val COL_ID = "id"
        const val COL_PAIS = "pais"
        const val COL_CIUDAD = "ciudad"
        const val COL_POBLACION = "poblacion"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_CAPITALES (
              $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
              $COL_PAIS TEXT NOT NULL,
              $COL_CIUDAD TEXT NOT NULL,
              $COL_POBLACION INTEGER
            );
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CAPITALES")
        onCreate(db)
    }
}