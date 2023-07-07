package com.example.camera

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.camera.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rePermissions()
        binding.btnCamera.setOnClickListener {
            openCamera()
        }
        binding.btnGallery.setOnClickListener {
            openGallery()
        }
        binding.imageView.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem =
                arrayOf("Select photo from Gallery", "Capture photo from Camera")
            pictureDialog.setItems(pictureDialogItem) { dialog, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> openCamera()
                }
            }
            pictureDialog.show()
        }

    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            // handle the result here
            if (uri != null) {
                displayImage(uri)
            }
            Log.e("image", "image is $uri")
        }

    private fun openGallery() {
        getContent.launch("image/*")
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess: Boolean ->
            if (isSuccess) {
                photoUri?.let { displayImage(it) }
                // handle the successful result here
            } else {
                // handle the failed result here
                Toast.makeText(this, "Capture Failed", Toast.LENGTH_SHORT).show()
            }
        }

    private fun displayImage(image: Uri) {
        binding.imageView.setImageURI(image)
    }

    private fun openCamera() {
        photoUri = getOutputMediaFileUri()
        takePicture.launch(photoUri)
    }

    private fun getOutputMediaFileUri(): Uri {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Camera Pro"
        )
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mediaFile = File(mediaStorageDir.path + File.separator + "IMG_" + timeStamp + ".jpg")
        return FileProvider.getUriForFile(
            this,
            "${BuildConfig.APPLICATION_ID}.provider",
            mediaFile
        )
    }

    private fun rePermissions() {
        val permissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false) -> {

                }

                permissions.getOrDefault(Manifest.permission.CAMERA, false) -> {

                }

                else -> {

                }
            }
        }
        permissionRequest.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )
    }
}