package com.example.backlogseries.watchlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.example.backlogseries.model.WatchlistSerie
import kotlin.math.roundToInt

@Composable
fun WatchlistScreen(
    watchlist: List<WatchlistSerie>,
    onSerieClick: (WatchlistSerie) -> Unit,
    onRemoveSerie: (WatchlistSerie) -> Unit,
    onUpdateSettings: (index: Int, minutesPerDay: Int) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedSerieIndex by remember { mutableStateOf(-1) }

    // Afficher le popup d'édition si une série est sélectionnée
    if (showEditDialog && selectedSerieIndex >= 0) {
        EditWatchlistDialog(
            watchlistSerie = watchlist[selectedSerieIndex],
            onDismiss = { showEditDialog = false },
            onSave = { minutesPerDay ->
                onUpdateSettings(selectedSerieIndex, minutesPerDay)
                showEditDialog = false
            }
        )
    }

    Column {
        if (watchlist.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Votre liste de lecture est vide")
            }
        } else {
            LazyColumn {
                itemsIndexed(watchlist) { index, watchlistSerie ->
                    WatchlistSerieItem(
                        watchlistSerie = watchlistSerie,
                        onClick = { onSerieClick(watchlistSerie) },
                        onRemove = { onRemoveSerie(watchlistSerie) },
                        onEdit = {
                            selectedSerieIndex = index
                            showEditDialog = true // Ouvrir le popup d'édition
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WatchlistStats(watchlist: List<WatchlistSerie>) {
    val totalEpisodes = watchlist.sumOf { it.serie.number_of_episodes ?: 0 }
    val totalDays = watchlist.sumOf { it.getDaysToComplete() }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Résumé de votre liste de lecture",
                fontWeight = FontWeight.Bold
            )
            Text(text = "${watchlist.size} séries · $totalEpisodes épisodes")
            Text(text = "Temps estimé : $totalDays jours (~${totalDays / 7} semaines)")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistSerieItem(
    watchlistSerie: WatchlistSerie,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w200${watchlistSerie.serie.poster_path}",
                contentDescription = "Poster",
                modifier = Modifier.size(100.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Informations
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = watchlistSerie.serie.name, fontWeight = FontWeight.Bold)
                Text(text = "Saisons: ${watchlistSerie.serie.number_of_seasons}, Épisodes: ${watchlistSerie.serie.number_of_episodes}")
                
                val daysToComplete = watchlistSerie.getDaysToComplete()
                val formattedRuntime = watchlistSerie.getFormattedTotalRuntime()
                Text(text = "Temps total : $formattedRuntime")
                Text(text = "Plan de visionnage : $daysToComplete jours")
            }
            
            // Actions
            Column {
                IconButton(onClick = { onEdit() }) {
                    Icon(Icons.Default.Edit, contentDescription = "Éditer")
                }
                IconButton(onClick = { onRemove() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer")
                }
            }
        }
    }
}

@Composable
fun EditWatchlistDialog(
    watchlistSerie: WatchlistSerie,
    onDismiss: () -> Unit,
    onSave: (minutesPerDay: Int) -> Unit
) {
    var minutes by remember { mutableStateOf(watchlistSerie.minutesPerDay) }
    var isDailyMode by remember { mutableStateOf(true) } // Toggle entre jour et semaine

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Modifier le temps de visionnage", fontWeight = FontWeight.Bold)

                // Toggle entre jour et semaine
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = { isDailyMode = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isDailyMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("Jour")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(
                        onClick = { isDailyMode = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (!isDailyMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text("Semaine")
                    }
                }

                // Champ pour modifier le temps de visionnage
                OutlinedTextField(
                    value = minutes.toString(),
                    onValueChange = { minutes = it.toIntOrNull() ?: 0 },
                    label = {
                        Text(if (isDailyMode) "Minutes par jour" else "Minutes par semaine")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    TextButton(onClick = {
                        // Convertir les minutes en minutes par jour si nécessaire
                        val minutesPerDay = if (isDailyMode) {
                            minutes
                        } else {
                            minutes / 7 // Conversion de semaine à jour
                        }
                        onSave(minutesPerDay)
                    }) {
                        Text("Sauvegarder")
                    }
                }
            }
        }
    }
}

