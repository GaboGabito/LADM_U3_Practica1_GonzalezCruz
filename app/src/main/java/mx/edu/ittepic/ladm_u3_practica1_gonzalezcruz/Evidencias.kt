package mx.edu.ittepic.ladm_u3_practica1_gonzalezcruz

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import android.graphics.Bitmap

class Evidencias(id: Int,img:ByteArray){
    var foto = img
    var id = 0
    var idForanea = id
    var error = -1

    val nombreBaseDatos = "actividades"
    var puntero : Context?=null

    fun asignarPuntero(p: Context){
        puntero = p
    }

    fun insertar():Boolean{
        error = -1
        try{
            var base = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var insertar = base.writableDatabase
            var datos = ContentValues()

            datos.put("FOTO", foto)
            datos.put("IDACTIVIDAD",idForanea)
            var respuesta = insertar.insert("EVIDENCIAS", "IDEVIDENCIA", datos)
            if(respuesta.toInt()==-1){
                error = 2
                return false
            }
        }catch (e: SQLiteException){
            error = 1
            return false
        }
        return true
    }

    fun mostrarTodos(id:String):ArrayList<ByteArray>{
        var data = ArrayList<ByteArray>()
        error = -1
        try {
            var base= BaseDatos(puntero!!,nombreBaseDatos,null,1)
            var select = base.readableDatabase
            var columnas = arrayOf("*")
            var idBuscar = arrayOf(id)
            var cursor = select.query("EVIDENCIAS",columnas,"IDACTIVIDAD=?",idBuscar,null,null,null)
            if(cursor.moveToFirst()){
                do{
                    data.add(cursor.getBlob(2))
                }while (cursor.moveToNext())
            }else{
                error = 3
            }

        }catch (e:SQLiteException){
            error=1
        }

        return data
    }

}