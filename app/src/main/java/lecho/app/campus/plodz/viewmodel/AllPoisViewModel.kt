package lecho.app.campus.plodz.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import lecho.app.campus.plodz.repository.PoiRepository

class AllPoisViewModel(val poiRepository: PoiRepository) : ViewModel() {

    lateinit var pois: LiveData<AllPois>

    fun init() {
        pois = poiRepository.getAllPois()
    }
}