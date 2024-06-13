package com.example.codigo_en_clase_kotlin

import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import utilidades.Personas
import utilidades.SQLiteConexion
import utilidades.Trans

class ActivityCombo : AppCompatActivity() {
    private lateinit var conexion: SQLiteConexion;
    private lateinit var comboPersonas: Spinner;
    private lateinit var nombres: EditText;
    private lateinit var apellidos: EditText;
    private lateinit var correo: EditText;
    private lateinit var lista: ArrayList<Personas>;
    private lateinit var Arreglo: ArrayList<String>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_combo)

        conexion = SQLiteConexion(this, Trans.DBname, null, Trans.Version);
        comboPersonas = findViewById(R.id.spinner) as Spinner;
        nombres = findViewById(R.id.cbNombre) as EditText;
        apellidos = findViewById(R.id.cbApellido) as EditText;
        correo = findViewById(R.id.cbCorreo) as EditText;

        obtenerInfo()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun obtenerInfo() {
        // Traer los datos de la base de datos para que sean leidos.
        val db = conexion.readableDatabase;

        // Crear un array que contenga los objetos del tipo "Personas".
        lista = ArrayList<Personas>();

        // Creamos un "Cursor" que permita ejecutar la consulta de SQL.
        val cursor: Cursor = db.rawQuery(Trans.SelectAllPerson, null);

        // El bucle while nos dice que mientras hayan filas en el cursor esta devolvera true
        // permitiendo que valla a la siguiente fila con el metodo ".moveToNext()".
        while (cursor.moveToNext()) {
            // Creamos una nueva instancia de "Personas"
            val person = Personas(
                // Para las demas lineas "seteamos" el valor dependiendo del "columnIndex" que tenga.
                id = cursor.getInt(0),
                nombres = cursor.getString(1),
                apellidos = cursor.getString(2),
                edad = cursor.getInt(3),
                correo = cursor.getString(4),
                foto = cursor.getString(5)
            );
            // En cada vuelta del bucle agregamos los datos al "ArrayList lista".
            lista.add(person);
        }
        // Cerramos el cursor, esto no afecta en nada al codigo pero es una
        // buena practica para guardar recursos.
        cursor.close();

        // El metodo MostrarPersonas cumple 2 funciones:
        // 1. Mostrar los datos de las personas en el "Spinner",
        // ejemplo: Persona 1, Persona 2
        // 2. Llenaria las filas de texto que declaramos arriba, serian:
        // nombres, apellidos, correo.
        mostrarPersonas();
    }

    fun mostrarPersonas() {
        Arreglo = ArrayList<String>();

        // El bucle for recorrera cada elemento del arreglo "lista"
        // que almacena la informacion de las personas que tenemos.
        lista.forEach { persona ->
            Arreglo.add("${persona.id} ${persona.nombres} ${persona.apellidos}");
        }

        // Creamos un nuevo "ArrayAdapter", esto para poder mostrarlo con el
        // simple_spinner_item, y aqui poder mostrar el nombre de las
        // personas.
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Arreglo);

        // Ahora aqui establecemos el "setDropDownViewResource", que hara posible
        // la vista desplegable.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Asignamos el "ArrayAdapter<String> adapter" al "combopersonas", esto
        // para mostrar los datos como el nombre.
        comboPersonas.adapter = adapter;

        // Con el "combopersonas" le decimos que cuando seleccionemos un item
        // con el metodo ".setOnItemSelectedListener()" haga algo.
        comboPersonas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // Declaramos una variable para saber la posicion en la que tiene que poner
                // los datos.
                val personaSeleccionada = lista[position];

                // Establecemos los datos en los campos de texto correspondientes.
                nombres.setText(personaSeleccionada.nombres);
                apellidos.setText(personaSeleccionada.apellidos);
                correo.setText(personaSeleccionada.correo);
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}