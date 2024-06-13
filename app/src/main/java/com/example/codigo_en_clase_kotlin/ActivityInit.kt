package com.example.codigo_en_clase_kotlin

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import utilidades.SQLiteConexion
import utilidades.Trans

class ActivityInit : AppCompatActivity() {
    private lateinit var nombres: EditText;
    private lateinit var apellidos: EditText;
    private lateinit var edad: EditText;
    private lateinit var correo: EditText;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_init)

        nombres = findViewById(R.id.nombre);
        apellidos = findViewById(R.id.apellido);
        edad = findViewById(R.id.edad);
        correo = findViewById(R.id.correo);
        val submitButton: Button = findViewById(R.id.submitButton);

        submitButton.setOnClickListener() {
            Agregar();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun Agregar() {
        try {
            val conexion = SQLiteConexion(this, Trans.DBname, null, Trans.Version);
            val db = conexion.writableDatabase;

            val valores = ContentValues();

            valores.put(Trans.nombres, nombres.text.toString());
            valores.put(Trans.apellidos, apellidos.text.toString());
            valores.put(Trans.edad, edad.text.toString());
            valores.put(Trans.correo, correo.text.toString());

            var resultado = db.insert(Trans.TablePersonas, Trans.id, valores);

            Toast.makeText(
                applicationContext,
                "Registro Ingresado Con Exito" + resultado.toString(),
                Toast.LENGTH_LONG
            ).show();

            db.close();
        } catch (ex: Exception) {
            ex.toString();
        }
    }
}
