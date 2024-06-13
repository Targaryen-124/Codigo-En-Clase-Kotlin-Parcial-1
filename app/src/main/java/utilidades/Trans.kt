package utilidades

object Trans {
    // Version De La Base De Datos.
    const val Version = 1;

    // Nombre De La Base De Datos
    const val DBname = "PM012P"

    // Nombre De La Base De Datos.
    const val TablePersonas = "personas";

    // Propiedades
    const val id = "id";
    const val nombres = "nombres";
    const val apellidos = "apellidos";
    const val edad = "edad";
    const val correo = "correo";
    const val foto = "foto";

    val CreateTablePersonas = """
        CREATE TABLE $TablePersonas (
            id INTEGER PRIMARY KEY AUTOINCREMENT, 
            nombres TEXT, 
            apellidos TEXT, 
            edad INTEGER, 
            correo TEXT, 
            foto TEXT
        )
    """.trimIndent()

    const val SelectAllPerson = "SELECT * FROM $TablePersonas";

    const val DropTablePersonas = "DROP TABLE  IF EXISTS $TablePersonas";
}