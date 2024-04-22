package com.rileybrewer.brewalliance.matches

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rileybrewer.brewalliance.events.CurrentEventViewModel
import com.rileybrewer.brewalliance.proto.CompLevel
import com.rileybrewer.brewalliance.proto.Event
import com.rileybrewer.brewalliance.proto.Match

/** Used to display a screen for a match. */
@Composable
fun MatchScreen(
    matchClick: (Match) -> Unit,
    currentEventViewModel: CurrentEventViewModel = hiltViewModel()
) {
    val currentEvent by currentEventViewModel.event.collectAsStateWithLifecycle(Event.getDefaultInstance())

    if (currentEvent == Event.getDefaultInstance()) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(size = 250.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    } else {
        MatchList(
            matches = currentEvent.matchesList,
            onClick = matchClick
        )
    }
}

/** Display an info card for a single match. */
@Composable
fun MatchCard(
    match: Match,
    onClick: (Match) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
            .clickable { onClick.invoke(match) }
    ) {
        Text(
            text = "Match: ${
                if (match.compLevel == CompLevel.SEMI_FINAL) {
                    match.setNumber
                } else {
                    match.matchNumber
                }
            }",
            style = MaterialTheme.typography.titleLarge
        )
        Row {
            Text (
                text = "Blue: ",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF000080)
            )
            Text (
                text = match.blueAllianceList.map { it.teamNumber }.joinToString(", "),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Row {
            Text (
                text = "Red: ",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF800000)
            )
            Text (
                text = match.redAllianceList.map { it.teamNumber }.joinToString(", "),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

/** Displays a list of matches */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchList(
    matches: List<Match>,
    onClick: (Match) -> Unit
) {
    LazyColumn {
        groupMatches(matches).forEach { (compLevel, matchList) ->

            stickyHeader {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(all = 8.dp)
                ) {
                    Text(
                        text = compLevel.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            items(matchList) { match ->
                MatchCard(match, onClick)
            }
        }
    }
}

/** Groups together matches into lists by competition level. */
fun groupMatches(matches: List<Match>): Map<CompLevel, List<Match>> {
    val sortingOrder = mapOf(
        CompLevel.QUALIFICATION to 0,
        CompLevel.EIGHT_FINAL to 1,
        CompLevel.QUARTER_FINAL to 2,
        CompLevel.SEMI_FINAL to 3,
        CompLevel.FINAL to 4,
    )
    return matches.groupBy { it.compLevel }
        .toSortedMap(compareBy<CompLevel> { sortingOrder[it] })
        .mapValues { (_, matches) ->
            matches.sortedWith( compareBy( { it.matchNumber }, { it.setNumber } ))
        }
}
