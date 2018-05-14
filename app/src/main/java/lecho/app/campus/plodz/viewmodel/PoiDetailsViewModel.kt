package lecho.app.campus.plodz.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

class PoiDetailsViewModel(var details: LiveData<PoiDetails>) : ViewModel()