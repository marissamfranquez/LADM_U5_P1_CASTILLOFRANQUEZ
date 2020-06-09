package com.example.ladm_u5_practica1_castillofranquezmarissa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var nom = ""
    var lon = 0.0
    var lat = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        var extras = intent.extras
        nom = extras!!.getString("nom").toString()
        lat = extras!!.getDouble("geolat")
        lon = extras!!.getDouble("geolon")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val lug = LatLng(lat, lon)
        mMap.addMarker(MarkerOptions().position(lug).title(nom))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lug))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lug, 18f))
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true
    }
}
