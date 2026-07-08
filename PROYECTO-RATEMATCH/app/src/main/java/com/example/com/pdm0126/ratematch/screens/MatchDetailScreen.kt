package com.example.com.pdm0126.ratematch.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.com.pdm0126.ratematch.data.model.Match
import com.example.com.pdm0126.ratematch.data.remote.dto.EventDto
import com.example.com.pdm0126.ratematch.data.remote.dto.TeamStatisticsDto
import com.example.com.pdm0126.ratematch.ui.viewmodel.MatchDetailState
import com.example.com.pdm0126.ratematch.ui.viewmodel.MatchDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    viewModel: MatchDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Partido") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (uiState is MatchDetailState.Success) {
                        val match = (uiState as MatchDetailState.Success).match
                        IconButton(onClick = { viewModel.toggleFavorite(!match.isFavorite) }) {
                            Icon(
                                imageVector = if (match.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (match.isFavorite) Color.Red else Color.Gray
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is MatchDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is MatchDetailState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is MatchDetailState.Success -> {
                    MatchDetailContent(
                        match = state.match,
                        statistics = state.statistics,
                        events = state.events,
                        onRateMatch = { rating ->
                            viewModel.rateMatch(rating) // Disparamos la acción de calificar
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MatchDetailContent(
    match: Match,
    statistics: List<TeamStatisticsDto>,
    events: List<EventDto>,
    onRateMatch: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Marcador y Equipos
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = match.leagueLogo,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = match.leagueName,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TeamColumn(name = match.homeTeam, logoUrl = match.homeLogo, modifier = Modifier.weight(1f))

                        Text(
                            text = "${match.scoreHome} - ${match.scoreAway}",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        TeamColumn(name = match.awayTeam, logoUrl = match.awayLogo, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = match.status,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Eventos (Goles)
        val goals = events.filter { it.type.lowercase() == "goal" }
        if (goals.isNotEmpty()) {
            item {
                Text(
                    text = "Goles",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold
                )
            }
            items(goals) { goal ->
                GoalEventRow(goal)
            }
        }

        // Estadísticas con Barras
        if (statistics.isNotEmpty()) {
            item {
                Text(
                    text = "Estadísticas",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            val homeStats = statistics.getOrNull(0)?.statistics ?: emptyList()
            val awayStats = statistics.getOrNull(1)?.statistics ?: emptyList()

            items(homeStats.size) { index ->
                val homeStat = homeStats[index]
                val awayStat = awayStats.find { it.type == homeStat.type }

                if (awayStat != null) {
                    StatBarRow(
                        label = homeStat.type,
                        homeValue = homeStat.value.toString().replace("\"", ""),
                        awayValue = awayStat.value.toString().replace("\"", "")
                    )
                }
            }
        } else {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Estadísticas no disponibles aún", color = Color.Gray)
                }
            }
        }

        // SECCIÓN DE CALIFICACIÓN (NUEVO)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Califica este partido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            RatingBar(
                rating = match.userRating, // Marcará error rojo hasta que actualicemos Match.kt
                onRatingChange = { newRating ->
                    onRateMatch(newRating)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = if (match.userRating > 0) "Tu calificación: ${match.userRating}/5" else "Aún no calificado",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// COMPONENTE DE ESTRELLAS (NUEVO)
@Composable
fun RatingBar(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Calificar $i estrellas",
                tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChange(i) }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun GoalEventRow(event: EventDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.SportsSoccer,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${event.time.elapsed}'",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(32.dp)
        )
        Text(
            text = event.player.name ?: "Jugador",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = event.team.name ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
fun StatBarRow(label: String, homeValue: String, awayValue: String) {
    val hVal = homeValue.replace("%", "").toDoubleOrNull() ?: 0.0
    val aVal = awayValue.replace("%", "").toDoubleOrNull() ?: 0.0
    val total = hVal + aVal
    val homeWeight = if (total > 0) (hVal / total).toFloat() else 0.5f

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = homeValue, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = awayValue, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(homeWeight.coerceAtLeast(0.01f))
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight((1f - homeWeight).coerceAtLeast(0.01f))
                    .background(MaterialTheme.colorScheme.secondary)
            )
        }
    }
}

@Composable
fun TeamColumn(name: String, logoUrl: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = logoUrl,
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}