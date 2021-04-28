package dev.polazzo.myphotos.model

import android.content.Context
import android.widget.Toast
import java.util.*

class DataModel private constructor() {
    companion object {
        val instance = DataModel()
    }

    private lateinit var database: PhotosDatabase
    private lateinit var photos: ArrayList<Photo>

    fun setContext(context: Context) {
        database = PhotosDatabase(context)
        photos = database.retrievePhotosFromDB()
    }

    fun getPhotos(): ArrayList<Photo> {
        return photos
    }

    fun addPhoto(photo: Photo, context: Context) {
        val id: Long = database.createPhotoInDB(photo)
        if (id > 0) {
            photo.copy(id = id)
            photos.add(0, photo)
        } else {
            Toast.makeText(
                context, "Add photo problem",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}