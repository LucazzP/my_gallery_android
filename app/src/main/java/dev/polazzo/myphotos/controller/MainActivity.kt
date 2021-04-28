package dev.polazzo.myphotos.controller

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.polazzo.myphotos.R
import dev.polazzo.myphotos.model.DataModel
import dev.polazzo.myphotos.model.Photo
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnClickPhotoListener {
    val CAMERA_PERMISSION_CODE = 2001
    val CAMERA_INTENT_CODE = 3001
    private lateinit var recyclerView: RecyclerView
    val PERMISSION_CODE_READ = 1001
//    val PERMISSION_CODE_WRITE = 1002
    private val PICK_INTENT_IMAGE_CODE = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DataModel.instance.setContext(this)
        recyclerView = findViewById(R.id.recyclerView)

        applyGridLayoutManager(2)
        val adapter = PhotoAdapter(DataModel.instance.getPhotos(), this)
        recyclerView.adapter = adapter

        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                applyGridLayoutManager(progress + 1)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    fun loadPictureClickButton(view: View){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

                requestPermissions(permission, PERMISSION_CODE_READ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
            } else {
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                gallery.type = "image/*"
                startActivityForResult(gallery, PICK_INTENT_IMAGE_CODE)
            }
        }

    }


    fun takePhotoClickButton(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestCameraPermission()
        } else {
            sendCameraIntent()
        }
    }

    fun applyGridLayoutManager(columns: Int) {
        // initialize grid layout manager
        GridLayoutManager(
            this, // context
            columns, // span count
            RecyclerView.VERTICAL, // orientation
            false // reverse layout
        ).apply {
            // specify the layout manager for recycler view
            recyclerView.layoutManager = this
        }
    }

    var picturePath: String? = null

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestCameraPermission() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            if (checkSelfPermission(Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA
                    ), CAMERA_PERMISSION_CODE
                )
            } else {
                sendCameraIntent()
            }
        } else {
            Toast.makeText(
                this@MainActivity,
                "No camera available", Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendCameraIntent()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Camera Permission Denied", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun sendCameraIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true)
        if (intent.resolveActivity(packageManager) != null) {
            val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
                .format(Date())
            val picName = "pic_$timeStamp"
            val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            var pictureFile: File? = null
            try {
                pictureFile = File.createTempFile(picName, ".jpg", dir)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (pictureFile != null) {
                picturePath = pictureFile.absolutePath
                val photoUri = FileProvider.getUriForFile(
                    this,
                    "dev.polazzo.myphotos.fileprovider",
                    pictureFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, CAMERA_INTENT_CODE)
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_INTENT_CODE) {
            if (resultCode == RESULT_OK) {
                val file = File(picturePath!!)
                if (file.exists() && picturePath != null) {
                    DataModel.instance.addPhoto(Photo(0, picturePath!!, file.name, Date()), this)
                    recyclerView.adapter!!.notifyItemInserted(0)
                }
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Problem getting the image from the camera app",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        if(requestCode == PICK_INTENT_IMAGE_CODE){
            if(resultCode == RESULT_OK){
                val imageUri = data?.data
                val file = File(getPath(imageUri!!)!!)
//                val file = File(imageUri?.path!!)
                DataModel.instance.addPhoto(Photo(0, file.path, file.name, Date()), this)
                recyclerView.adapter!!.notifyItemInserted(0)
            }else {
                Toast.makeText(
                    this@MainActivity,
                    "Problem getting the image from the gallery app",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }
    fun getPath(uri: Uri): String? {
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null) ?: return null
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(column_index)
        cursor.close()
        return s
    }

    override fun onClickPhotoListener(photo: Photo, position: Int) {
        //TODO("Not yet implemented")
    }
}