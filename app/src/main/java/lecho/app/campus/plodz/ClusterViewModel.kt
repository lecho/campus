package lecho.app.campus.plodz

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

class ClusterViewModel(var poisData: LiveData<List<Poi>>) : ViewModel()