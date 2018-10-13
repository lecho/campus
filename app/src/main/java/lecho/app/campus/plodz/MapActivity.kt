package lecho.app.campus.plodz

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import lecho.app.campus.plodz.repository.PoiRepository
import lecho.app.campus.plodz.viewmodel.AllPois
import lecho.app.campus.plodz.viewmodel.AllPoisViewModel


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        @JvmField
        val TAG: String = MapActivity::class.java.simpleName
        val DEFAULT_LATlNG = LatLng(51.7505298, 19.4551516)
        const val DEFAULT_ZOOM = 14.8F
    }

    private lateinit var mMap: GoogleMap
    private lateinit var allPoisViewModel: AllPoisViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        @Suppress("CAST_NEVER_SUCCEEDS")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
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
            allPois!!.pois.forEach { poi ->
                val position = poi.latLong.toMapsLatLng()
                mMap.addMarker(MarkerOptions().position(position).title(poi.name))
            }
            val position = allPois.pois[0].latLong.toMapsLatLng()
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position))
        })

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LATlNG, DEFAULT_ZOOM))
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
