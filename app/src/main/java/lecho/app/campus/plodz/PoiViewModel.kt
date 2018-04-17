package lecho.app.campus.plodz

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

class PoiViewModel(var poiData: LiveData<Poi>) : ViewModel()