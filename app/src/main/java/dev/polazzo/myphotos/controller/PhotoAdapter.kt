package dev.polazzo.myphotos.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import dev.polazzo.myphotos.R
import dev.polazzo.myphotos.model.Photo
import java.io.File

internal class PhotoAdapter internal constructor(
    private val itemList: ArrayList<Photo>, private val onClickPhotoListener: OnClickPhotoListener
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textNamePhoto)
        val photoView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.activity_card_photo, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: PhotoAdapter.ViewHolder, position: Int) {
        val photo = itemList[position]
        val nameTextView = viewHolder.nameTextView
        nameTextView.text = photo.name
        val photoView = viewHolder.photoView
        photoView.setImageURI(File(photo.imagePath).toUri())
        viewHolder.itemView.setOnClickListener {
            onClickPhotoListener.onClickPhotoListener(photo, position)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}

interface OnClickPhotoListener {
    fun onClickPhotoListener(photo: Photo, position: Int)
}
