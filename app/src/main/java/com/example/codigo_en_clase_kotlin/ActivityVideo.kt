package com.example.codigo_en_clase_kotlin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.ByteArrayOutputStream
import java.io.IOException

class ActivityVideo : AppCompatActivity() {
    private val solicitarVideo = 101;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_video)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val comenzarGrabacion = findViewById<Button>(R.id.buttonGrabar);

        comenzarGrabacion.setOnClickListener {
            val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            if (videoIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(videoIntent, solicitarVideo)
            } else {
                Toast.makeText(this@ActivityVideo, "No hay cámara.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == solicitarVideo && resultCode == RESULT_OK) {
            val videoUri: Uri? = data?.data
            Toast.makeText(this, "Guardado en $videoUri", Toast.LENGTH_LONG).show()
            try {
                val base64Video = convertVideoTo64(this, videoUri)
                Log.i("Video", base64Video)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Error al convertir", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Error al grabar", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun convertVideoTo64(context: Context, videoUri: Uri?): String {
        val inputStream = context.contentResolver.openInputStream(videoUri!!)
        val byteArrayOutputStream = ByteArrayOutputStream()

        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream?.read(buffer).also { bytesRead = it!! } != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead)
        }

        val videoBytes = byteArrayOutputStream.toByteArray()
        inputStream?.close()
        byteArrayOutputStream.close()

        return Base64.encodeToString(videoBytes, Base64.DEFAULT)
    }
}