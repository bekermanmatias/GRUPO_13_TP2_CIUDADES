package com.example.misciudades.data

import android.content.ContentValues
import android.content.Context
import com.example.misciudades.data.DBHelper.Companion.COL_CIUDAD
import com.example.misciudades.data.DBHelper.Companion.COL_ID
import com.example.misciudades.data.DBHelper.Companion.COL_PAIS
import com.example.misciudades.data.DBHelper.Companion.COL_POBLACION
import com.example.misciudades.data.DBHelper.Companion.TABLE_CAPITALES

data class Capital(
    val id: Int,
    val pais: String,
    val ciudad: String,
    val poblacion: Int
)

class CapitalRepository(context: Context) {
    private val dbHelper = DBHelper(context)

    /** 1. Insertar nueva capital */
    fun insertar(pais: String, ciudad: String, poblacion: Int) {
        val db = dbHelper.writableDatabase
        db.insert(
            DBHelper.TABLE_CAPITALES,
            null,
            ContentValues().apply {
                put(DBHelper.COL_PAIS, pais)
                put(DBHelper.COL_CIUDAD, ciudad)
                put(DBHelper.COL_POBLACION, poblacion)
            }
        )
        db.close()
    }

    /** 2a. Consultar capital por ID */
    fun consultarPorId(id: Int): Capital? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DBHelper.TABLE_CAPITALES,
            null,
            "${DBHelper.COL_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        val cap = if (cursor.moveToFirst()) {
            Capital(
                id        = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)),
                pais      = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PAIS)),
                ciudad    = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CIUDAD)),
                poblacion = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_POBLACION))
            )
        } else null
        cursor.close()
        db.close()
        return cap
    }

    /** 2b. Consultar capital por ciudad (nombre) */
    /** Busca todas las capitales cuyo nombre comience por el prefijo dado (case‑insensitive) */
    fun buscarPorPrefijo(prefijo: String): List<Capital> {
        val db = dbHelper.readableDatabase
        // El signo '?' es sustituido por 'prefijo%'
        val cursor = db.query(
            TABLE_CAPITALES,
            null,
            "$COL_CIUDAD LIKE ?",
            arrayOf("$prefijo%"),
            null, null, null
        )
        val lista = mutableListOf<Capital>()
        if (cursor.moveToFirst()) {
            do {
                lista += Capital(
                    id        = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    pais      = cursor.getString(cursor.getColumnIndexOrThrow(COL_PAIS)),
                    ciudad    = cursor.getString(cursor.getColumnIndexOrThrow(COL_CIUDAD)),
                    poblacion = cursor.getInt(cursor.getColumnIndexOrThrow(COL_POBLACION))
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }


    /** 3. Listar todas */
    fun listarTodas(): List<Capital> {
        val lista = mutableListOf<Capital>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DBHelper.TABLE_CAPITALES} ORDER BY ${DBHelper.COL_PAIS}, ${DBHelper.COL_CIUDAD}",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                lista += Capital(
                    id        = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)),
                    pais      = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PAIS)),
                    ciudad    = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CIUDAD)),
                    poblacion = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_POBLACION))
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    /** 4. Borrar por ciudad */
    fun borrarPorCiudad(ciudad: String) {
        val db = dbHelper.writableDatabase
        db.delete(
            DBHelper.TABLE_CAPITALES,
            "${DBHelper.COL_CIUDAD} = ?",
            arrayOf(ciudad)
        )
        db.close()
    }

    /** 5. Borrar todas las de un país */
    fun borrarPorPais(pais: String) {
        val db = dbHelper.writableDatabase
        db.delete(
            DBHelper.TABLE_CAPITALES,
            "${DBHelper.COL_PAIS} = ?",
            arrayOf(pais)
        )
        db.close()
    }

    /** 6. Actualizar capital por ID */
    fun actualizarCapital(
        id: Int,
        nuevoPais: String,
        nuevaCiudad: String,
        nuevaPoblacion: Int
    ) {
        val db = dbHelper.writableDatabase
        db.update(
            DBHelper.TABLE_CAPITALES,
            ContentValues().apply {
                put(DBHelper.COL_PAIS, nuevoPais)
                put(DBHelper.COL_CIUDAD, nuevaCiudad)
                put(DBHelper.COL_POBLACION, nuevaPoblacion)
            },
            "${DBHelper.COL_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
    }
}
