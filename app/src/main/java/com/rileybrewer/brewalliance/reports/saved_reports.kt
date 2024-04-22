package com.rileybrewer.brewalliance.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rileybrewer.brewalliance.proto.Report
import com.rileybrewer.brewalliance.proto.Reports
import com.rileybrewer.brewalliance.share.ShareBottomSheet

/** Display a list of reports on the device. */
@Composable
fun SavedReports(
    onReport: (Report) -> Unit,
    reportViewModel: ReportViewModel = hiltViewModel()
) {
    val reports by reportViewModel.reports.collectAsStateWithLifecycle(
        Reports.getDefaultInstance()
    )
    var shareText by remember { mutableStateOf("") }

    LazyColumn(
        Modifier.fillMaxWidth()
    ) {
        items(reports.reportsMap.toList()) { report ->
            SwipeableBox(
                onSwiped = {
                    reportViewModel.deleteReport(report.second)
                },
                background = {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .padding(start = 24.dp),
                            imageVector = Icons.Outlined.Delete,
                            tint = MaterialTheme.colorScheme.onError,
                            contentDescription = ""
                        )
                    }
                }
            ) {
                SavedReport(
                    report = report.second,
                    onView = {
                        onReport(report.second)
                    },
                    onShare = {
                        shareText = String(
                            report.second.toByteString().toByteArray()
                        )
                    }
                )
            }
        }
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(all = 8.dp)
            ) {
                Text(
                    text = "Archived",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        items(reports.archivedMap.toList()) { report ->
            SwipeableBox(
                onSwiped = {
                    reportViewModel.unDeleteReport(report.second)
                },
                background = {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .padding(start = 24.dp),
                            imageVector = Icons.Outlined.Restore  ,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = ""
                        )
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ReportCard(report.second)
                }
            }
        }
    }
    ShareBottomSheet(
        shareText = shareText,
        onDismiss = {
            shareText = ""
        }
    )
}

/** Shows a user-swipeable row. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableBox(
    onSwiped: () -> Unit,
    background: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState()

    when (swipeState.currentValue) {
        SwipeToDismissBoxValue.StartToEnd -> {
            onSwiped()
            LaunchedEffect(swipeState) {
                swipeState.snapTo(SwipeToDismissBoxValue.Settled)
            }
        }
        else -> {}
    }

    SwipeToDismissBox(
        state = swipeState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = false,
        backgroundContent = background
    ) {
        content.invoke()
    }
}

/** Simple display for a single report. */
@Composable
fun ReportCard(
    report: Report
) {
    Column(
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text ="Match: ${report.matchKey.split("_").last()}",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Team: ${report.teamKey.removePrefix("frc")}",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Scout: ${report.scoutKey}",
            style = MaterialTheme.typography.titleSmall
        )
    }
}

/** Shows a report along with a button for sharing. */
@Composable
fun SavedReport(
    report: Report,
    onView: () -> Unit,
    onShare: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onView() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReportCard(report)
        Spacer(
            modifier = Modifier.weight(1f)
        )
        IconButton(
            modifier = Modifier.minimumInteractiveComponentSize(),
            onClick = onShare
        ) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = ""
            )
        }
    }
}
