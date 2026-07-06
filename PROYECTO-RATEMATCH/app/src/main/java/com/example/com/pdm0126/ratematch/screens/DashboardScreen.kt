package com.example.com.pdm0126.ratematch.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.com.pdm0126.ratematch.data.model.Match
import com.example.com.pdm0126.ratematch.ui.viewmodel.DashboardState
import com.example.com.pdm0126.ratematch.ui.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToMatchDetail: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLiveOnly by viewModel.isLiveOnly.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RateMatch") },
                actions = {
                    IconButton(onClick = { viewModel.refreshMatches() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    FilterChip(
                        selected = isLiveOnly,
                        onClick = { viewModel.toggleLiveFilter() },
                        label = { Text("En Vivo") }
                    )
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.changeDate(-1) }) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Atrás")
                }

                val dateLabel = when (selectedDate) {
                    LocalDate.now() -> "Hoy"
                    LocalDate.now().minusDays(1) -> "Ayer"
                    LocalDate.now().plusDays(1) -> "Mañana"
                    else -> selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                }

                Text(
                    text = dateLabel,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { viewModel.changeDate(1) }) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Adelante")
                }
            }

            when (val state = uiState) {
                is DashboardState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is DashboardState.Success -> {
                    val filteredMatches = if (isLiveOnly) {
                        state.matches.filter { it.status == "LIVE" || it.status == "IN_PLAY" }
                    } else {
                        state.matches
                    }

                    if (filteredMatches.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay partidos disponibles.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredMatches) { match ->
                                MatchItem(
                                    match = match,
                                    onClick = { onNavigateToMatchDetail(match.id) },
                                    onHideClick = { viewModel.toggleMatchHidden(match.id) }
                                )
                            }
                        }
                    }
                }
                is DashboardState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchItem(
    match: Match,
    onClick: () -> Unit,
    onHideClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${match.homeTeam} vs ${match.awayTeam}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Estado: ${match.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(onClick = onHideClick) {
                Text("Ocultar")
            }
        }
    }
}