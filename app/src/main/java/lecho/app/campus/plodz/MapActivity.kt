package lecho.app.campus.plodz

import android.arch.lifecycle.Observer
import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import android.arch.lifecycle.ViewModelProviders
import lecho.app.campus.plodz.repository.PoiRepository
import lecho.app.campus.plodz.viewmodel.AllPois
import lecho.app.campus.plodz.viewmodel.AllPoisViewModel


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        @JvmField
        val TAG: String = MapActivity::class.java.simpleName
    }

    private lateinit var mMap: GoogleMap
    private lateinit var allPoisViewModel: AllPoisViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        allPoisViewModel = ViewModelProviders.of(this).get(AllPoisViewModel::class.java)
        allPoisViewModel.init(PoiRepository())
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        changeMapStyle(R.raw.map_style)

        allPoisViewModel.pois.observe(this, Observer<AllPois> { allPois ->
            allPois!!.pois.forEach { it ->
                val position = it.latLong.toMapsLatLng()
                mMap.addMarker(MarkerOptions().position(position).title(it.name))
            }
            val position = allPois.pois.get(0).latLong.toMapsLatLng()
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
        })

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun changeMapStyle(styleJson: Int) {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, styleJson))
            if (!success) Log.e(TAG, "Style parsing failed.")
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }
}
