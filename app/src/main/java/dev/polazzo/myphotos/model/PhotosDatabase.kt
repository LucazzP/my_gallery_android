package dev.polazzo.myphotos.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class PhotosDatabase(private val context: Context?) : SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    DB_VERSION
) {
    companion object {
        private const val DB_NAME = "cities.sqlite"
        private const val DB_VERSION = 1
        private const val DB_TABLE = "Photos"
        private const val COL_ID = "id"
        private const val COL_NAME = "name"
        private const val COL_IMAGE_PATH = "image_path"
        private const val COL_CREATED_AT = "created_at"
    }

    /*
    CREATE
    RETRIEVE
    UPDATE
    DELETE
    CRUD
     */

    override fun onCreate(db: SQLiteDatabase?) {
        val query: String = String.format(
            "CREATE TABLE IF NOT EXISTS %s(" +
                    " %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s LONG)",
            DB_TABLE,
            COL_ID,
            COL_NAME,
            COL_IMAGE_PATH,
            COL_CREATED_AT,
        )

        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    //CRUD
    //CREATE - CRIA UMA CITY NO BD
    fun createPhotoInDB(photo: Photo): Long {
        val database = writableDatabase
        val values = ContentValues()
        values.put(COL_NAME, photo.name)
        values.put(COL_IMAGE_PATH, photo.imagePath)
        values.put(COL_CREATED_AT, Date().time)
        val id = database.insert(DB_TABLE, null, values)
        database.close()
        return id
    }

    //RETRIEVE - TRAZER OS DADOS DO BD
    fun retrievePhotosFromDB(): ArrayList<Photo> {
        val database = readableDatabase
        val cursor = database.query(
            DB_TABLE, null, null,
            null, null, null, COL_CREATED_AT
        )
        val cities: ArrayList<Photo> = ArrayList<Photo>()
        if (cursor.moveToFirst()) {
            do {
                val id =
                    cursor.getLong(cursor.getColumnIndex(COL_ID))
                val name =
                    cursor.getString(cursor.getColumnIndex(COL_NAME))
                val imagePath =
                    cursor.getString(cursor.getColumnIndex(COL_IMAGE_PATH))
                val createdAt =
                    cursor.getLong(cursor.getColumnIndex(COL_CREATED_AT))
                val c = Photo(id, imagePath, name, Date(createdAt))
                cities.add(c)
            } while (cursor.moveToNext())
        }
        cursor.close()
        database.close()
        return cities
    }

    fun updatePhotoInDb(photo: Photo): Long {
        val database = writableDatabase
        val values = ContentValues()
        values.put(COL_NAME, photo.name)
        values.put(COL_IMAGE_PATH, photo.imagePath)
        values.put(COL_CREATED_AT, photo.createdAt.time)
        val id = database.update(
            DB_TABLE,
            values,
            "$COL_ID = ?",
            arrayOf(java.lang.String.valueOf(photo.id))
        ).toLong()
        database.close()
        return id
    }

    fun deletePhotoInDb(photoId: Long) {
        val database = writableDatabase
        database.delete(
            DB_TABLE,
            "$COL_ID = ?",
            arrayOf(photoId.toString())
        )
        database.close()
    }
}