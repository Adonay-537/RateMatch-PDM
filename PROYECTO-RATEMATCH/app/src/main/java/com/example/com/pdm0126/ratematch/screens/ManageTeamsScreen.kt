package com.example.com.pdm0126.ratematch.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.com.pdm0126.ratematch.ui.viewmodel.SelectableItem
import com.example.com.pdm0126.ratematch.ui.viewmodel.SelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTeamsScreen(
    viewModel: SelectionViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredItems = items.filter { it.name.contains(searchQuery, ignoreCase = true) }
    val groupedItems = filteredItems.groupBy { it.category }

    LaunchedEffect(Unit) {
        viewModel.loadTeams()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Equipos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar equipo...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = MaterialTheme.shapes.medium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        groupedItems.forEach { (category, teams) ->
                            item {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(teams) { team ->
                                SelectionItem(
                                    item = team,
                                    onToggle = { viewModel.toggleSelection(team.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionItem(
    item: SelectableItem,
    onToggle: () -> Unit
) {
    Surface(
        onClick = onToggle,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(item.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Checkbox(checked = item.isSelected, onCheckedChange = { onToggle() })
        }
    }
}
