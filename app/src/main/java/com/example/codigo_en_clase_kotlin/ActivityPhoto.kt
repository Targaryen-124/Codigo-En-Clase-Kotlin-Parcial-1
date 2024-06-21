package com.example.codigo_en_clase_kotlin

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

class ActivityPhoto : AppCompatActivity() {
    private val peticionAccesoCamera: Int = 101
    private val peticionCapturaImagen: Int = 102
    private var currentPhotoPath: String? = null
    private var image64: String? = null
    private var ObjetoImagen: ImageView? = null
    private var btnCaptura: Button? = null;
    private var pathImagen: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_photo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ObjetoImagen = findViewById(R.id.imageView)
        btnCaptura = findViewById(R.id.btntakefoto)

        btnCaptura?.setOnClickListener {
            permisos()
        }
    }

    private fun permisos() {
        if (ContextCompat.checkSelfPermission(
                applicationContext, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.CAMERA), peticionAccesoCamera
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == peticionAccesoCamera) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(applicationContext, "Acceso Denegado!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == peticionCapturaImagen && resultCode == Activity.RESULT_OK) {
            setPic()
            galleryAddPic()

            val bitmap = BitmapFactory.decodeFile(currentPhotoPath)

            if (bitmap != null) {
                image64 = convertImageTo64(bitmap)
                Log.i("Imagen", image64!!)
            } else {
                Log.e("Error", "Bitmap is null")
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp"

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null

            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            photoFile?.let {
                val photoURI = FileProvider.getUriForFile(
                    this, "com.example.codigo_en_clase_kotlin.fileprovider", it
                )

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, peticionCapturaImagen)
            }
        }
    }

    private fun galleryAddPic() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(currentPhotoPath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        sendBroadcast(mediaScanIntent)
    }
    private fun setPic() {
        // Get the dimensions of the View
        val targetW = ObjetoImagen!!.width
        val targetH = ObjetoImagen!!.height

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)

        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = max(1, min(photoW / targetW, photoH / targetH))

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        ObjetoImagen?.setImageBitmap(bitmap)
    }

    private fun convertImageTo64(bitmap: Bitmap): String {
        val byteImage = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteImage)
        val byteArray = byteImage.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}