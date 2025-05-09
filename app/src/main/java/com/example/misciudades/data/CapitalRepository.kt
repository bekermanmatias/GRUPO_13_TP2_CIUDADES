package com.example.misciudades.data

import android.content.ContentValues
import android.content.Context

// Data class para mapear resultados
data class Capital(
    val id: Int,
    val pais: String,
    val ciudad: String,
    val poblacion: Int
)

class CapitalRepository(context: Context) {
    private val dbHelper = DBHelper(context)

    // 1. Insertar nueva capital
    fun insertar(pais: String, ciudad: String, poblacion: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COL_PAIS, pais)
            put(DBHelper.COL_CIUDAD, ciudad)
            put(DBHelper.COL_POBLACION, poblacion)
        }
        db.insert(DBHelper.TABLE_CAPITALES, null, values)
        db.close()
    }

    // 2. Consultar capital por ciudad
    fun consultarPorCiudad(ciudadBuscada: String): Capital? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DBHelper.TABLE_CAPITALES,
            null,
            "${DBHelper.COL_CIUDAD} = ?",
            arrayOf(ciudadBuscada),
            null,
            null,
            null
        )
        val capital = if (cursor.moveToFirst()) {
            Capital(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)),
                pais = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PAIS)),
                ciudad = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CIUDAD)),
                poblacion = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_POBLACION))
            )
        } else null
        cursor.close()
        db.close()
        return capital
    }

    // 2b. Consultar capital por ID
    fun consultarPorId(id: Int): Capital? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DBHelper.TABLE_CAPITALES,
            null,
            "${DBHelper.COL_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        val capital = if (cursor.moveToFirst()) {
            Capital(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)),
                pais = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PAIS)),
                ciudad = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CIUDAD)),
                poblacion = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_POBLACION))
            )
        } else null
        cursor.close()
        db.close()
        return capital
    }

    // 3. Borrar por ciudad
    fun borrarPorCiudad(ciudad: String) {
        val db = dbHelper.writableDatabase
        db.delete(
            DBHelper.TABLE_CAPITALES,
            "${DBHelper.COL_CIUDAD} = ?",
            arrayOf(ciudad)
        )
        db.close()
    }

    // 4. Borrar todas las de un país
    fun borrarPorPais(pais: String) {
        val db = dbHelper.writableDatabase
        db.delete(
            DBHelper.TABLE_CAPITALES,
            "${DBHelper.COL_PAIS} = ?",
            arrayOf(pais)
        )
        db.close()
    }

    // 5. Actualizar poblacion
    fun actualizarPoblacion(ciudad: String, nuevaPoblacion: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COL_POBLACION, nuevaPoblacion)
        }
        db.update(
            DBHelper.TABLE_CAPITALES,
            values,
            "${DBHelper.COL_CIUDAD} = ?",
            arrayOf(ciudad)
        )
        db.close()
    }

    // Listar todas
    fun listarTodas(): List<Capital> {
        val lista = mutableListOf<Capital>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ${DBHelper.TABLE_CAPITALES} ORDER BY ${DBHelper.COL_PAIS}, ${DBHelper.COL_CIUDAD}",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Capital(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID)),
                        pais = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_PAIS)),
                        ciudad = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CIUDAD)),
                        poblacion = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_POBLACION))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
}
