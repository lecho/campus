package lecho.app.campus.plodz.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import lecho.app.campus.plodz.viewmodel.AllPois

class PoiRepository {

    fun getAllPois(): LiveData<AllPois> {
        val data = MutableLiveData<AllPois>()
        return data
    }
}