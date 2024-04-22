package com.rileybrewer.brewalliance.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.util.Log
import com.rileybrewer.brewalliance.events.EventDataSource
import com.rileybrewer.brewalliance.reports.ReportsDataSource
import com.rileybrewer.brewalliance.proto.Event
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking

private const val REPORT = "report"
private const val CURRENT_EVENT = "current_event"
private const val AUTHORITY = "com.rileybrewer.brewalliance.contentprovider"
private const val CODE_REPORTS_DIR = 1
private const val CODE_CURRENT_EVENT_DIR = 2
private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
    addURI(AUTHORITY, REPORT, CODE_REPORTS_DIR)
    addURI(AUTHORITY, CURRENT_EVENT, CODE_CURRENT_EVENT_DIR)
}

/**
 * Provides a way to retrieve and insert reports.
 */
class ReportProvider: ContentProvider() {
    private lateinit var eventDataSource: EventDataSource
    private lateinit var reportsDataSource: ReportsDataSource

    override fun onCreate(): Boolean {
        setupDataSources()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        when (sUriMatcher.match(uri)) {
            CODE_REPORTS_DIR -> {
                context?.let { context ->
                    setupDataSources()
                    return MatrixCursor(
                        arrayOf(
                            "key",
                            "match_key",
                            "team_key",
                            "scout_key",
                            "device_key",
                            "time_start",
                            "auto:pre_load",
                            "auto:leave",
                            "auto:collect",
                            "auto:score_speaker",
                            "auto:score_amp",
                            "tele:collect",
                            "tele:score_speaker",
                            "tele:score_amp",
                            "end:park",
                            "end:climb",
                            "end:trap"
                        )
                    ).apply {
                        setNotificationUri(context.contentResolver, uri)
                        runBlocking {
                            reportsDataSource.reports.first().reportsMap.forEach{ (_, report) ->
                                addRow(
                                    arrayOf(
                                        report.key,
                                        report.matchKey,
                                        report.teamKey,
                                        report.scoutKey,
                                        report.deviceKey,
                                        report.timeStart,
                                        report.auto.preLoad,
                                        report.auto.leave,
                                        report.auto.collect,
                                        report.auto.scoreSpeaker,
                                        report.auto.scoreAmp,
                                        report.tele.collect,
                                        report.tele.scoreSpeaker,
                                        report.tele.scoreAmp,
                                        report.endGame.park,
                                        report.endGame.climb.name,
                                        report.endGame.trap
                                    )
                                )
                            }
                        }
                    }
                }
                Log.e("ReportProvider", "No context")
                return null
            }
            CODE_CURRENT_EVENT_DIR -> {
                context?.let { context ->
                    setupDataSources()
                    return MatrixCursor(
                        arrayOf("current_event")
                    ).apply {
                        setNotificationUri(context.contentResolver, uri)
                        runBlocking {
                            addRow(
                                arrayOf(
                                    eventDataSource.currentEvent.last().toByteArray()
                                )
                            )
                            eventDataSource.currentEvent

                        }
                    }
                }
                Log.e("ReportProvider", "No context")
                return null
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d("ReportProvider", "URI: $uri")
        Log.d("ReportProvider", "values: $values")
        when (sUriMatcher.match(uri)) {
            CODE_CURRENT_EVENT_DIR -> {
                context?.let { context ->
                    setupDataSources()
                    runBlocking {
                        Log.d("ReportProvider", "eventString: ${values?.getAsString("event")}")

                        values?.getAsByteArray("event")?.let {
                            Log.d("ReportProvider", "eventBytes: $it")
                            eventDataSource.update(
                                Event.parseFrom(it),
                                listOf(),
                                listOf()
                            )
                        }
                    }
                    return uri
                }
                Log.e("ReportProvider", "No context")
                return null
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw IllegalArgumentException("Deletes not allowed")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw IllegalArgumentException("Updates not allowed")
    }

    override fun getType(uri: Uri): String? {
        return when (sUriMatcher.match(uri)) {
            CODE_REPORTS_DIR -> "vnd.android.cursor.dir/$AUTHORITY.$REPORT"
            CODE_CURRENT_EVENT_DIR -> "vnd.android.cursor.dir/$AUTHORITY.$CURRENT_EVENT"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    private fun setupDataSources() {
        context?.let { context ->
            if (!this::reportsDataSource.isInitialized) {
                reportsDataSource = EntryPoints.get(context, ProviderEntryPoint::class.java).reportsDataSource()
            }
            if (!this::eventDataSource.isInitialized) {
                eventDataSource = EntryPoints.get(context, ProviderEntryPoint::class.java).eventDataSource()
            }
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ProviderEntryPoint {
    fun reportsDataSource(): ReportsDataSource
    fun eventDataSource(): EventDataSource
}