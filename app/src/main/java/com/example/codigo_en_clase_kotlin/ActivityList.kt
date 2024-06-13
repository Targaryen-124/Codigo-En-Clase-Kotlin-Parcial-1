package com.example.codigo_en_clase_kotlin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import utilidades.Personas
import utilidades.SQLiteConexion
import utilidades.Trans

class ActivityList : AppCompatActivity() {
    private lateinit var conexion: SQLiteConexion;
    private lateinit var listPerson: ListView;
    private lateinit var lista: ArrayList<Personas>;
    private lateinit var arreglo: ArrayList<String>;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list)

        conexion = SQLiteConexion(this, Trans.DBname, null, Trans.Version);
        listPerson = findViewById(R.id.listPerson);

        ObtenerInfo();

        val adp = ArrayAdapter(this, android.R.layout.simple_list_item_1,arreglo);
        listPerson.adapter = adp;

        listPerson.setOnItemClickListener { parent, view, position, id ->
            val elementoSeleccionado = parent.getItemAtPosition(position) as String;
            Toast.makeText(applicationContext, elementoSeleccionado, Toast.LENGTH_LONG).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun ObtenerInfo() {
        val db = conexion.readableDatabase;
        lista = ArrayList();

        val cursor = db.rawQuery(Trans.SelectAllPerson, null);

        while (cursor.moveToNext()) {
            val person = Personas(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getString(4),
                null
            );
            lista.add(person);
        }
        cursor.close();
        fillData();
    }

    private fun fillData() {
        arreglo = ArrayList();

        lista.forEach { persona ->
            arreglo.add("${persona.id} ${persona.nombres} ${persona.apellidos}");
        }
    }
}

