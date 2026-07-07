package com.example.com.pdm0126.ratematch.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.com.pdm0126.ratematch.data.model.Match
import com.example.com.pdm0126.ratematch.ui.viewmodel.DashboardState
import com.example.com.pdm0126.ratematch.ui.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

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
    val context = LocalContext.current

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val newDate = LocalDate.of(year, month + 1, dayOfMonth)
            viewModel.setDate(newDate)
        },
        selectedDate.year,
        selectedDate.monthValue - 1,
        selectedDate.dayOfMonth
    )

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

                Surface(
                    onClick = { datePickerDialog.show() },
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = dateLabel,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                IconButton(onClick = { viewModel.changeDate(1) }) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Adelante")
                }
            }

            when (val state = uiState) {
                is DashboardState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DashboardState.Success -> {
                    val filteredMatches = if (isLiveOnly) {
                        state.matches.filter { it.status == "LIVE" || it.status == "IN_PLAY" || it.status == "1H" || it.status == "2H" || it.status == "HT" }
                    } else {
                        state.matches
                    }

                    if (filteredMatches.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay partidos disponibles.")
                        }
                    } else {
                        val groupedByLeague = filteredMatches.groupBy { it.leagueName }
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            groupedByLeague.forEach { (leagueName, matches) ->
                                item {
                                    LeagueHeader(leagueName)
                                }
                                items(matches) { match ->
                                    MatchItem(
                                        match = match,
                                        onClick = { onNavigateToMatchDetail(match.id) },
                                        onHideScoreClick = { viewModel.toggleScoreVisibility(match.id) }
                                    )
                                }
                            }
                        }
                    }
                }
                is DashboardState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.refreshMatches() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeagueHeader(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchItem(
    match: Match,
    onClick: () -> Unit,
    onHideScoreClick: () -> Unit,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = match.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    val localTime = match.getLocalTime()
                    if (localTime.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = localTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!match.isHidden) {
                    Text(
                        text = "${match.scoreHome} - ${match.scoreAway}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                } else {
                    Text(
                        text = "? - ?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
                
                IconButton(onClick = onHideScoreClick) {
                    Icon(
                        imageVector = if (match.isHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (match.isHidden) "Mostrar resultado" else "Ocultar resultado"
                    )
                }
            }
        }
    }
}
