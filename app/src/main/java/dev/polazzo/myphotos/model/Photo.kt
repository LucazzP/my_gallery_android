package dev.polazzo.myphotos.model

import java.util.*

class Photo(val id: Long, val imagePath: String, val name: String, val createdAt: Date) {
    fun copy(
        id: Long = this.id,
        imagePath: String = this.imagePath,
        name: String = this.name,
        createdAt: Date = this.createdAt
    ) =
        Photo(id, imagePath, name, createdAt)
}