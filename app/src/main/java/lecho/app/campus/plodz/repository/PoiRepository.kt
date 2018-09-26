package lecho.app.campus.plodz.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import lecho.app.campus.plodz.tmp.MockDatabase
import lecho.app.campus.plodz.viewmodel.AllPois

class PoiRepository {

    fun getAllPois(): LiveData<AllPois> {
        val data = MutableLiveData<AllPois>()
        data.value = MockDatabase.allPois
        return data
    }
}