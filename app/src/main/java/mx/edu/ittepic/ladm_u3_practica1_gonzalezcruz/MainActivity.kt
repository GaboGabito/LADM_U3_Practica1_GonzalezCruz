package mx.edu.ittepic.ladm_u3_practica1_gonzalezcruz

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    var listaID = ArrayList<String>()
    var bitmap : Bitmap?= null
    private val PHOTO_SELECTED = 1
    var imagen:Dialog ?= null
    var infoAct =""
    var act_actual = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        capturar.setOnClickListener {
            var actividad = Actividades(descripcion.text.toString(), fechaCaptura.text.toString(), fechaEntrega.text.toString())
            actividad.asignarPuntero(this)
            var resultado = actividad.insertar()
            if(resultado==true){
                mensaje("SE CAPTURO ACTIVIDAD")
                cargarLista()
                descripcion.setText("")
                fechaCaptura.setText("")
                fechaEntrega.setText("")
            }else{
                when(actividad.error){
                    1 -> {dialogo(" error en tabla, no se creó o no se conectó a base de datos")}
                    2-> {dialogo("error no se pudo insertar")}
                }
            }
        }
        cargarLista()
    }
    fun cargarLista(){
        try{
            var conexion = Actividades("","","")
            conexion.asignarPuntero(this)
            var data = conexion.mostrarTodos()

            if(data.size == 0){
                if(conexion.error == 3){
                    dialogo("No se pudo realizar consulta por tabla vacia")
                }
                return
            }

            var total = data.size-1
            var vector = Array<String>(data.size,{""})
            listaID = ArrayList<String>()
            (0..total).forEach {
                var actividad = data[it]
                var item = "\nDescripcion: "+actividad.descripcion+"\nFecha Captura: "+
                        actividad.fechaCaptura+"\nFecha Entrega: "+actividad.fechaEntrega
                vector[it] = item
                listaID.add(actividad.id.toString())
            }
            lista.adapter  = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, vector)
            lista.setOnItemClickListener { parent, view, position, id ->
                var con = Actividades("","","")
                con.asignarPuntero(this)
                var actividadEncontrada = con.buscar(listaID[position])
                act_actual =actividadEncontrada.id
                if(con.error==4){
                    dialogo("Error no se encontró ID")
                    return@setOnItemClickListener
                }
                infoAct="\nID:" +actividadEncontrada.id+"\n\n"+
                        "Descripción: "+actividadEncontrada.descripcion+"\n\n"+
                        "Fecha de captura: "+actividadEncontrada.fechaCaptura + "\n\n" +
                        "Fecha de entrega: "+actividadEncontrada.fechaEntrega+"\n"

                AlertDialog.Builder(this).setTitle("DETALLES")
                    .setMessage(infoAct)
                    .setPositiveButton("Ver Evidencias"){d, i->
                        agregarEvidencia()
                        d.dismiss()
                    }
                    .setNegativeButton("Eliminar"){d, i->
                       android.app.AlertDialog.Builder(this)
                           .setTitle("Atención")
                           .setMessage("¿Desea borrar esta actividad?")
                           .setPositiveButton("Aceptar"){d,i->
                               eliminarActividad(act_actual.toString())
                               cargarLista()
                               d.dismiss()
                           }
                           .setNegativeButton("Cancelar"){d,i->
                               d.cancel()
                           }.show()
                    }
                    .setNeutralButton("Cancelar"){d,i->
                        d.cancel()
                    }
                    .show()

            }
        }catch (e:Exception){
            dialogo(e.message.toString())
        }
    }

    private fun agregarEvidencia() {
        imagen = Dialog(this)
        imagen?.setContentView(R.layout.evidencias)
        var btnAgregarEvidencia = imagen?.findViewById<Button>(R.id.AgregarE)
        var btnCancelarEv = imagen?.findViewById<Button>(R.id.CancelarE)
        var lblInfo=imagen?.findViewById<TextView>(R.id.datosActividad)

        btnAgregarEvidencia?.setOnClickListener {
            abrirGaleria()
        }
        btnCancelarEv?.setOnClickListener {
            imagen!!.cancel()
        }

        imagen?.show()
        lblInfo?.setText(infoAct)
        cargarImagenes()
    }

    fun abrirGaleria(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PHOTO_SELECTED)
    }
    fun cargarImagenes(){
        val arr = byteArrayOf(0)
        try{
            var conexion = Evidencias(0,arr)
            conexion.asignarPuntero(this)
            var data=conexion.mostrarTodos(act_actual.toString())
            if(data.size==0){
                if(conexion.error==3){
                    //dialogo("No se pudo realizar consulta o tabla vacia")
                }
                return
            }
            var total=data.size-1
            var listaEvidencias = imagen?.findViewById<LinearLayout>(R.id.llBotonera)
            (0..total).forEach{
                var evidencia= data[it]
                var img= ImageView(this)
                img.setImageBitmap(ByteArrayToBitmap(evidencia))
                listaEvidencias?.addView(img)
            }
        }catch (e:Exception){
            dialogo(e.message.toString())
        }
    }
    fun ByteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
        val arrayInputStream = ByteArrayInputStream(byteArray)
        return BitmapFactory.decodeStream(arrayInputStream)
    }
    fun mensaje(s:String){
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }
    fun dialogo(s:String){
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setPositiveButton("OK"){d, i->}
            .show()
    }
    fun eliminarActividad(id:String){
        var conexion = Actividades("","","")
        conexion.asignarPuntero(this)
        var resultado = conexion.eliminar(id)
        if(resultado==true){
            mensaje("Se eliminó actividad")

        }else{
            when(conexion.error){
                1->{
                    dialogo("Error en tabla, no se creó o no se conectó a la base de datos")
                }
                2->{
                    dialogo("Error: No se pudo eliminar")
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PHOTO_SELECTED) {
            val selectedImage: Uri? = data?.data
            if( selectedImage==null){
                return}
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)

            var listaEvidencias = imagen?.findViewById<LinearLayout>(R.id.llBotonera)
            val img= ImageView(this)
            img.setImageBitmap(bitmap)
            listaEvidencias?.addView(img)

            val bos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, bos)
            val bArray: ByteArray = bos.toByteArray()

            insertarEvidencia(act_actual,bArray)
        }
    }
    fun insertarEvidencia(id_a :Int,img:ByteArray){
        var evidencia = Evidencias(id_a,img)
        evidencia.asignarPuntero(this)
        var resultado = evidencia.insertar()
        if(resultado==true){
            mensaje("Se capturó evidencia")
            cargarLista()
        }else{
            when(evidencia.error){
                1->{
                    dialogo("Error en tabla, no se creó o no se conectó a la base de datos")
                }
                2->{
                    dialogo("Error: No se pudo insertar")
                }
            }
        }

    }
}
