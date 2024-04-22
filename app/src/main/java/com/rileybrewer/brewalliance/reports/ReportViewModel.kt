package com.rileybrewer.brewalliance.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rileybrewer.brewalliance.proto.Report
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportsDataSource: ReportsDataSource
): ViewModel() {
    val reports = reportsDataSource.reports

    fun saveReport(report: Report) {
        viewModelScope.launch {
            reportsDataSource.put(report)
        }
    }

    fun deleteReport(report: Report) {
        viewModelScope.launch {
            reportsDataSource.archive(report)
        }
    }
    fun unDeleteReport(report: Report) {
        viewModelScope.launch {
            reportsDataSource.unArchive(report)
        }
    }
}
