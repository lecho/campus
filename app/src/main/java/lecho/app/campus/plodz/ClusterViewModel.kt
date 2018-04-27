package lecho.app.campus.plodz

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import lecho.app.campus.plodz.database.Poi

class ClusterViewModel(var poisData: LiveData<List<Poi>>) : ViewModel()