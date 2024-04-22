package com.rileybrewer.brewalliance.reports

import android.util.Log
import androidx.datastore.core.DataStore
import com.rileybrewer.brewalliance.proto.Report
import com.rileybrewer.brewalliance.proto.Reports
import java.io.IOException
import javax.inject.Inject

/** Data source accessing reports. */
class ReportsDataSource @Inject constructor(
    private val reportsDataStore: DataStore<Reports>
) {
    /** The current (device) reports. */
    val reports = reportsDataStore.data

    /**
     * Add a report to the current reports. If a report with this key already exists, it will be
     * overwritten.
     */
    suspend fun put(report: Report) {
        try {
            reportsDataStore.updateData {
                it.toBuilder()
                    .putReports(report.key, report)
                    .build()
            }
        } catch (ioException: IOException) {
            Log.e("ReportsDataSource", "Failed to update reports.", ioException)
        }
    }

    /** Moves the provided report from saved reports to archived reports. */
    suspend fun archive(report: Report) {
        try {
            reportsDataStore.updateData { currentReports ->
                currentReports.toBuilder()
                    .removeReports(report.key)
                    .putArchived(report.key, report)
                    .build()
            }
        } catch (ioException: IOException) {
            Log.e("ReportsDataSource", "Failed to delete report.", ioException)
        }
    }

    /** Moves the provided report from archived reports to saved reports. */
    suspend fun unArchive(report: Report) {
        try {
            reportsDataStore.updateData { currentReports ->
                currentReports.toBuilder()
                    .putReports(report.key, report)
                    .removeArchived(report.key)
                    .build()
            }
        } catch (ioException: IOException) {
            Log.e("ReportsDataSource", "Failed to un-delete report.", ioException)
        }
    }
}
