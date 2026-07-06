package com.example.com.pdm0126.ratematch.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.com.pdm0126.ratematch.ui.viewmodel.SelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageNationalTeamsScreen(
    viewModel: SelectionViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.items.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredItems = items.filter { it.name.contains(searchQuery, ignoreCase = true) }

    LaunchedEffect(Unit) {
        viewModel.loadNationalTeams()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Selecciones", fontWeight = FontWeight.Bold) },
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
                        placeholder = { Text("Buscar selección...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = MaterialTheme.shapes.medium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredItems) { nation ->
                            SelectionItem(
                                item = nation,
                                onToggle = { viewModel.toggleSelection(nation.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
