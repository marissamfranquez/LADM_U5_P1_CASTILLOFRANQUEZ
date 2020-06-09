package com.example.ladm_u5_practica1_castillofranquezmarissa

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()
    var posicion = ArrayList<Data>()
    lateinit var locacion : LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        var array : ArrayList<String> = ArrayList()

        baseRemota.collection("tecnologico")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Toast.makeText(
                        this,
                        "ERROR: " + firebaseFirestoreException.message,
                        Toast.LENGTH_SHORT
                    )
                    return@addSnapshotListener
                }

                array.clear()
                posicion.clear()

                for (document in querySnapshot!!) {
                    var data = Data()
                    data.nombre = document.getString("nombre").toString()
                    data.posicion1 = document.getGeoPoint("posicion1")!!
                    data.posicion2 = document.getGeoPoint("posicion2")!!
                    posicion.add(data)
                    array.add(data.nombre)
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, array)
                lista.adapter = adapter
            }

        locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var oyente = Oyente(this)
        locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 01f, oyente)

        lista.setOnItemClickListener { parent, view, position, id ->
            baseRemota.collection("tecnologico")
                .whereEqualTo("nombre", array.get(position))
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Toast.makeText(this, "NO HAY CONEXION", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    var mensaje = ""

                    var n = ""
                    var g = GeoPoint(0.0, 0.0)

                    for(document in  querySnapshot!!){
                        var nombre = document.getString("nombre").toString()
                        var lat = document.getGeoPoint("posicion1")!!.latitude
                        var lon = document.getGeoPoint("posicion1")!!.longitude

                        n = nombre
                        g = document.getGeoPoint("posicion1")!!

                        mensaje = "EDIFICIO: ${document.getString("nombre")} \n\n ESTA EN: \n" +
                                "De [${document.getGeoPoint("posicion1")!!.latitude}, ${document.getGeoPoint("posicion1")!!.longitude}] A \n" +
                                "[${document.getGeoPoint("posicion2")!!.latitude}, ${document.getGeoPoint("posicion2")!!.longitude}]\n\n" +
                                "Más: ${document.getGeoPoint("mas")}"
                    }

                    AlertDialog.Builder(this)
                        .setMessage(mensaje)
                        .setPositiveButton("VER EN EL MAPA") {d, p ->
                            var otraVentana = Intent(this, MapsActivity::class.java)
                            otraVentana.putExtra("geolat", g.latitude)
                            otraVentana.putExtra("geolon", g.longitude)
                            otraVentana.putExtra("nom", n)
                            startActivity(otraVentana)
                        }
                        .setNegativeButton("CANCELAR") {d, p -> }
                        .show()
                }
        }
    }
}

class Oyente(puntero : MainActivity) : LocationListener {
    var p = puntero

    override fun onLocationChanged(location: Location) {
        var geoPosicionGPS = GeoPoint(location.latitude, location.longitude)
        for(item in p.posicion) {
            if(item.estoyEn(geoPosicionGPS)) {
                p.textView3.setText("LUGAR: " + item.nombre)
            } else {
                p.textView3.setText("LUGAR: NINGUN LUGAR CONOCIDO")
            }
        }
        p.textView2.setText("ESTAS EN:\n[${location.latitude}, ${location.longitude}]")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }
}