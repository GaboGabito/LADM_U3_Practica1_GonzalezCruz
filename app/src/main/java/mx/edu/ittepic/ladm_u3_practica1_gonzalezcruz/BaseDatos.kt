package mx.edu.ittepic.ladm_u3_practica1_gonzalezcruz

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
): SQLiteOpenHelper(context, name, factory, version){
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE ACTIVIDADES(IDACTIVIDAD INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPCION VARCHAR(200), FECHACAPTURA DATE, FECHAENTREGA DATE)")
        db?.execSQL("CREATE TABLE EVIDENCIAS(IDEVIDENCIA INTEGER PRIMARY KEY AUTOINCREMENT, IDACTIVIDAD INTEGER NOT NULL, FOTO BLOB,FOREIGN KEY(IDACTIVIDAD) REFERENCES EVIDENCIAS(ATCTIVIDADES))")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}