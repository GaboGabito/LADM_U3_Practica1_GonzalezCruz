package mx.edu.ittepic.ladm_u3_practica1_gonzalezcruz

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException


class Actividades(d:String,fC:String,fE:String){
    var descripcion = d
    var fechaCaptura = fC
    var fechaEntrega = fE
    var id = 0
    var error = -1

    val nombreBaseDatos = "actividades"
    var puntero : Context ?=null

    fun asignarPuntero(p:Context){
        puntero = p
    }

    fun insertar():Boolean{
        error = -1
        try{
            var base = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var insertar = base.writableDatabase
            var datos = ContentValues()

            datos.put("DESCRIPCION", descripcion)
            datos.put("FECHACAPTURA", fechaCaptura)
            datos.put("FECHAENTREGA", fechaEntrega)
            var respuesta = insertar.insert("ACTIVIDADES", "IDACTIVIDADES", datos)
            if(respuesta.toInt()==-1){
                error = 2
                return false
            }
        }catch (e:SQLiteException){
            error = 1
            return false
        }
        return true
    }
    fun mostrarTodos():ArrayList<Actividades>{
        var data = ArrayList<Actividades>()
        error = -1
        try{
            var base = BaseDatos(puntero!!,nombreBaseDatos,null,1)
            var select = base.readableDatabase
            var columnas = arrayOf("*")


            var cursor = select.query("ACTIVIDADES",columnas,null,null,null,null,null,null)
            if(cursor.moveToFirst()){
                do{
                    var trabajadorTemporal = Actividades(cursor.getString(1),cursor.getString(2),cursor.getString(3))
                    trabajadorTemporal.id = cursor.getInt(0)
                    data.add(trabajadorTemporal)
                }while (cursor.moveToNext())
            }else{
                error = 3
            }
        }catch (e:SQLiteException){
            error = 1
        }
        return data
    }
    fun buscar(id:String):Actividades{
        var actividadEncontrada = Actividades("-1","-1","-1")

        error = -1
        try{
            var base = BaseDatos(puntero!!,nombreBaseDatos,null,1)
            var select = base.readableDatabase
            var columnas = arrayOf("*")
            var idBuscar = arrayOf(id)
            var cursor = select.query("ACTIVIDADES",columnas, "IDACTIVIDAD = ?", idBuscar, null,null,null)

            if(cursor.moveToNext()){
                actividadEncontrada.id = id.toInt()
                actividadEncontrada.descripcion = cursor.getString(1)
                actividadEncontrada.fechaCaptura = cursor.getString(2)
                actividadEncontrada.fechaEntrega = cursor.getString(3)

            }else{error = 4}
        }catch(e:SQLiteException){
            error = 1
        }
        return actividadEncontrada
    }
    fun eliminar(id:String):Boolean{
        error = -1
        try {
            var base = BaseDatos(puntero!!, nombreBaseDatos, null, 1)
            var eliminar = base.writableDatabase
            var idEliminar = arrayOf(id.toString())

            var respuesta = eliminar.delete("ACTIVIDADES", "IDACTIVIDAD = ?",idEliminar)
            var respuesta2 =eliminar.delete("EVIDENCIA","IDACTIVIDAD=?",idEliminar)
            if(respuesta==-1&&respuesta2==-1){
                error = 6
                return false
            }
        }catch (e:SQLiteException){
            error = 1
            return false
        }

        return true
    }
}