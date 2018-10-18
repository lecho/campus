package lecho.app.campus.plodz.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lecho.app.campus.plodz.tmp.MockDatabase
import lecho.app.campus.plodz.viewmodel.MapData

class PoiRepository {

    fun getAllPois(): LiveData<MapData> {
        val data = MutableLiveData<MapData>()
        data.value = MockDatabase.MAP_DATA
        return data
    }
}