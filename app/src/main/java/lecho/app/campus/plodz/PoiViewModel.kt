package lecho.app.campus.plodz

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import lecho.app.campus.plodz.database.entity.Poi

class PoiViewModel(var poiData: LiveData<Poi>) : ViewModel()