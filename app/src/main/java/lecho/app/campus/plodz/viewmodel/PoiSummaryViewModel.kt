package lecho.app.campus.plodz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class PoiSummaryViewModel(var summary: LiveData<PoiSummary>) : ViewModel()