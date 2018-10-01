package lecho.app.campus.plodz.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import lecho.app.campus.plodz.repository.PoiRepository

// TODO consider constructor with parameter and ViewModelFactory
class AllPoisViewModel : ViewModel() {

    private lateinit var poiRepository: PoiRepository
    lateinit var pois: LiveData<AllPois>

    fun init(poiRepository: PoiRepository) {
        this.poiRepository = poiRepository
        pois = poiRepository.getAllPois()
    }
}