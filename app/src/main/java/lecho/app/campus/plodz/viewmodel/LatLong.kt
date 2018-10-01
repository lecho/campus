package lecho.app.campus.plodz.viewmodel

import com.google.android.gms.maps.model.LatLng

data class LatLong(val latitude: Double,
                   val longitude: Double) {

    fun toMapsLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }
}