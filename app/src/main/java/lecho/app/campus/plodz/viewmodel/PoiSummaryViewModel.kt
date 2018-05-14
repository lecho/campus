package lecho.app.campus.plodz.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

class PoiSummaryViewModel(var summary: LiveData<PoiSummary>) : ViewModel()