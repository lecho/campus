package lecho.app.campus.plodz.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import lecho.app.campus.plodz.tmp.MockDatabase
import lecho.app.campus.plodz.viewmodel.AllPois

class PoiRepository {

    fun getAllPois(): LiveData<AllPois> {
        val data = MutableLiveData<AllPois>()
        data.value = MockDatabase.allPois
        return data
    }
}