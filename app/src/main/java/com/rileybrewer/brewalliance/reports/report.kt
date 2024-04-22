package com.rileybrewer.brewalliance.reports

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rileybrewer.brewalliance.proto.Autonomous
import com.rileybrewer.brewalliance.proto.Climb
import com.rileybrewer.brewalliance.proto.EndGame
import com.rileybrewer.brewalliance.proto.Report
import com.rileybrewer.brewalliance.proto.Settings
import com.rileybrewer.brewalliance.proto.Teleop
import com.rileybrewer.brewalliance.settings.SettingsViewModel

/** Displays info and inputs for a scouting report. */
@Composable
fun ReportScreen(
    initialReport: Report,
    onComplete: () -> Unit,
    reportViewModel: ReportViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    var report by remember { mutableStateOf(initialReport) }

    val settings by settingsViewModel.settings.collectAsStateWithLifecycle(
        initialValue = Settings.getDefaultInstance()
    )

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    reportViewModel.saveReport(
                        report.toBuilder()
                            .setScoutKey(
                                settings.scoutKey.ifEmpty {
                                    "UNKNOWN"
                                }
                            )
                            .setDeviceKey(
                                settings.deviceKey.ifEmpty {
                                    "UNKNOWN"
                                }
                            )
                            .build()
                    )
                    onComplete()
                },
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Check, "Done")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { internalPadding ->
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(internalPadding)
        ) {
            ExpandableSection(
                title = "Auto"
            ) {
                AutoReport(
                    autonomous = report.auto,
                    onChange = {
                        report = report.toBuilder().setAuto(it).build()
                    }
                )
            }
            ExpandableSection(title = "Tele") {
                TeleReport(
                    teleop = report.tele,
                    onChange = {
                        report = report.toBuilder().setTele(it).build()
                    }
                )
            }
            ExpandableSection(title = "End Game") {
                EndGameReport(
                    endGame = report.endGame,
                    onChange = {
                        report = report.toBuilder().setEndGame(it).build()
                    }
                )
            }
        }
    }
}

/** An expandable header control. */
@Composable
fun ExpandableHeader(
    modifier: Modifier,
    isExpanded: Boolean,
    title: String
) {
    val icon = if (isExpanded) {
        Icons.Rounded.KeyboardArrowUp
    } else {
        Icons.Rounded.KeyboardArrowDown
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement  =  Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        Image(
            modifier = modifier.size(32.dp),
            imageVector = icon,
            contentDescription = ""
        )
    }
}

/** An expandable content section that displays only a header until clicked. */
@Composable
fun ExpandableSection(
    title: String,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            Modifier.fillMaxWidth()
        ) {
            ExpandableHeader(
                Modifier.clickable { isExpanded = !isExpanded },
                isExpanded = isExpanded,
                title = title
            )
            AnimatedVisibility(visible = isExpanded) {
                content()
            }
        }
    }
}

/** Displays info and inputs for the autonomous period. */
@Composable
fun AutoReport(
    autonomous: Autonomous,
    onChange: (Autonomous) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement  =  Arrangement.SpaceBetween
        ) {
            Text(
                text = "Pre-loaded game piece",
                style = MaterialTheme.typography.titleLarge
            )
            Checkbox(
                checked = autonomous.preLoad,
                onCheckedChange = {
                    onChange.invoke(
                        autonomous.toBuilder()
                            .setPreLoad(!autonomous.preLoad)
                            .build()
                    )
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement  =  Arrangement.SpaceBetween
        ) {
            Text(
                text = "Leave",
                style = MaterialTheme.typography.titleLarge
            )
            Checkbox(
                checked = autonomous.leave,
                onCheckedChange = {
                    onChange.invoke(
                        autonomous.toBuilder()
                            .setLeave(!autonomous.leave)
                            .build()
                    )
                }
            )
        }
        UpDown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = "Collect: ",
            count = autonomous.collect,
            numberChanged = {
                onChange.invoke(
                    autonomous.toBuilder()
                        .setCollect(it)
                        .build()
                )
            }
        )
        UpDown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = "Score Speaker: ",
            count = autonomous.scoreSpeaker,
            numberChanged = {
                onChange.invoke(
                    autonomous.toBuilder()
                        .setScoreSpeaker(it)
                        .build()
                )
            }
        )
        UpDown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = "Score Amp: ",
            count = autonomous.scoreAmp,
            numberChanged = {
                onChange.invoke(
                    autonomous.toBuilder()
                        .setScoreAmp(it)
                        .build()
                )
            }
        )
    }
}

/** Displays info and inputs for the end game period. */
@Composable
fun EndGameReport(
    endGame: EndGame,
    onChange: (EndGame) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Park",
                style = MaterialTheme.typography.titleLarge
            )
            Checkbox(
                checked = endGame.park,
                onCheckedChange = {
                    onChange.invoke(
                        endGame.toBuilder()
                            .setPark(!endGame.park)
                            .build()
                    )
                }
            )
        }
        UpDown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = "Trap: ",
            count = endGame.trap,
            numberChanged = {
                onChange.invoke(
                    endGame.toBuilder()
                        .setTrap(it)
                        .build()
                )
            }
        )
        Text(
            text = "Climb",
            style = MaterialTheme.typography.titleLarge
        )
        listOf(Climb.NONE, Climb.SINGLE, Climb.DOUBLE, Climb.TRIPLE).forEach { climb ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (endGame.climb == climb),
                        onClick = {
                            onChange.invoke(
                                endGame
                                    .toBuilder()
                                    .setClimb(climb)
                                    .build()
                            )
                        }
                    )
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                RadioButton(
                    selected = (endGame.climb == climb),
                    onClick = {
                        onChange.invoke(
                            endGame.toBuilder().setClimb(climb).build()
                        )
                    }
                )
                Text(
                    text = climb.toString()
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/** Displays info and inputs for the tele-operated period. */
@Composable
fun TeleReport(
    teleop: Teleop,
    onChange: (Teleop) -> Unit
) {
    Column {
        UpDown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = "Collect: ",
            count = teleop.collect,
            numberChanged = {
                onChange.invoke(
                    teleop.toBuilder()
                        .setCollect(it)
                        .build()
                )
            }
        )
        UpDown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = "Score Speaker: ",
            count = teleop.scoreSpeaker,
            numberChanged = {
                onChange.invoke(
                    teleop.toBuilder()
                        .setScoreSpeaker(it)
                        .build()
                )
            }
        )
        UpDown(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = "Score Amp: ",
            count = teleop.scoreAmp,
            numberChanged = {
                onChange.invoke(
                    teleop.toBuilder()
                        .setScoreAmp(it)
                        .build()
                )
            }
        )
    }
}

/** A row for displaying an integer that can be adjusted up and down. */
@Composable
fun UpDown(
    modifier: Modifier,
    text: String,
    count: Int,
    numberChanged: (Int) -> Unit
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement  =  Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { numberChanged.invoke(count + 1) }
        ) {
            Image(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Rounded.Add,
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                contentDescription = ""
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge
        )
        Button(
            onClick = { numberChanged.invoke(0.coerceAtLeast(count - 1)) }
        ) {
            Image(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.Rounded.Remove,
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                contentDescription = ""
            )
        }
    }
}
