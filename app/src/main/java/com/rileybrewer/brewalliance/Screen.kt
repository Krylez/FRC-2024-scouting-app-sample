package com.rileybrewer.brewalliance

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rileybrewer.brewalliance.events.EventScreen
import com.rileybrewer.brewalliance.home.Home
import com.rileybrewer.brewalliance.importer.ReportImportScreen
import com.rileybrewer.brewalliance.matches.MatchScreen
import com.rileybrewer.brewalliance.proto.Event
import com.rileybrewer.brewalliance.proto.Match
import com.rileybrewer.brewalliance.proto.Report
import com.rileybrewer.brewalliance.reports.ReportScreen
import com.rileybrewer.brewalliance.reports.SavedReports
import com.rileybrewer.brewalliance.settings.Settings
import com.rileybrewer.brewalliance.teams.AllianceList
import java.util.UUID

enum class Screen(@StringRes val title: Int) {
    Home(R.string.screen_home),
    Events(R.string.screen_events),
    Matches(R.string.screen_matches),
    Teams(R.string.screen_teams),
    SavedReports(R.string.screen_saved_reports),
    NewReport(R.string.screen_new_report),
    ImportReport(R.string.screen_import_report),
    Settings(R.string.screen_settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    screen: Screen,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(screen.title),
                style = MaterialTheme.typography.headlineLarge
            )
        },
        modifier = modifier
    )
}

@Composable
fun App (
    navHostController: NavHostController = rememberNavController()
) {
    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: Screen.Home.name
    )

    var event by remember { mutableStateOf(Event.getDefaultInstance()) }
    var match by remember { mutableStateOf(Match.getDefaultInstance()) }
    var report by remember { mutableStateOf(Report.getDefaultInstance()) }

    Scaffold(
        topBar = { AppBar(currentScreen) }
    ) { innerPadding ->
        NavHost(
            navController = navHostController,
            startDestination = Screen.Home.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = Screen.Home.name
            ) {
                Home(
                    onViewEvents = {
                        navHostController.navigate(Screen.Events.name)
                    },
                    onImportReport = {
                        navHostController.navigate(Screen.ImportReport.name)
                    },
                    onViewMatches = {
                        navHostController.navigate(Screen.Matches.name)
                    },
                    onViewReports = {
                        navHostController.navigate(Screen.SavedReports.name)
                    },
                    onViewSettings = {
                        navHostController.navigate(Screen.Settings.name)
                    }
                )
            }
            composable(
                route = Screen.Events.name
            ) {
                EventScreen(
                    eventClick = {
                        event = it
                        navHostController.navigate(Screen.Matches.name)
                    }
                )
            }
            composable(
                route = Screen.Matches.name
            ) {
                MatchScreen(
                    matchClick = {
                        match = it
                        navHostController.navigate(Screen.Teams.name)
                    }
                )
            }
            composable(
                route = Screen.SavedReports.name
            ) {
                SavedReports(
                    onReport = {
                        report = it
                        navHostController.navigate(Screen.NewReport.name)
                    }
                )
            }
            composable(
                route = Screen.Teams.name
            ) {
                AllianceList(
                    match = match,
                    onClick = {
                        report = Report.newBuilder()
                            .setKey(UUID.randomUUID().toString())
                            .setMatchKey(match.key)
                            .setTeamKey(it.key)
                            .build()
                        navHostController.navigate(Screen.NewReport.name)
                    }
                )
            }
            composable(
                route = Screen.NewReport.name
            ) {
                ReportScreen(
                    initialReport = report,
                    onComplete = {
                        navHostController.popBackStack(Screen.Matches.name, true)
                    }
                )
            }
            composable(
                route = Screen.ImportReport.name
            ) {
                ReportImportScreen(onComplete = { navHostController.navigateUp() })
            }
            composable(
                route = Screen.Settings.name
            ) {
                Settings()
            }
        }
    }
}
