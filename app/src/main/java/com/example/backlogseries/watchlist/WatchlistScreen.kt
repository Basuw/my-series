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
    onUpdateSettings: (index: Int, episodesPerDay: Int, episodesPerWeek: Int, minutesPerDay: Int) -> Unit
) {
    var editingIndex by remember { mutableStateOf(-1) }
    var showEditDialog by remember { mutableStateOf(false) }
    
    Column {
        if (watchlist.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Votre liste de lecture est vide")
            }
        } else {
            // Statistiques de visionnage
            WatchlistStats(watchlist)
            
            // Liste des séries
            LazyColumn {
                itemsIndexed(watchlist) { index, watchlistSerie ->
                    WatchlistSerieItem(
                        watchlistSerie = watchlistSerie,
                        onClick = { onSerieClick(watchlistSerie) },
                        onRemove = { onRemoveSerie(watchlistSerie) },
                        onEdit = {
                            editingIndex = index
                            showEditDialog = true
                        }
                    )
                }
            }
        }
    }
    
    // Dialogue d'édition des paramètres de visionnage
    if (showEditDialog && editingIndex >= 0 && editingIndex < watchlist.size) {
        val watchlistSerie = watchlist[editingIndex]
        var episodesPerDay by remember { mutableStateOf(watchlistSerie.episodesPerDay) }
        var episodesPerWeek by remember { mutableStateOf(watchlistSerie.episodesPerWeek) }
        var minutesPerDay by remember { mutableStateOf(watchlistSerie.minutesPerDay) }
        
        Dialog(
            onDismissRequest = { showEditDialog = false }
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Planifier le visionnage de ${watchlistSerie.serie.name}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Paramètres de visionnage
                    Text(text = "Épisodes par jour :")
                    Slider(
                        value = episodesPerDay.toFloat(),
                        onValueChange = { episodesPerDay = it.roundToInt() },
                        valueRange = 1f..10f,
                        steps = 9
                    )
                    Text(text = "$episodesPerDay épisodes/jour")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(text = "Épisodes par semaine :")
                    Slider(
                        value = episodesPerWeek.toFloat(),
                        onValueChange = { episodesPerWeek = it.roundToInt() },
                        valueRange = 1f..30f,
                        steps = 29
                    )
                    Text(text = "$episodesPerWeek épisodes/semaine")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(text = "Minutes par jour :")
                    Slider(
                        value = minutesPerDay.toFloat(),
                        onValueChange = { minutesPerDay = it.roundToInt() },
                        valueRange = 30f..240f,
                        steps = 7
                    )
                    Text(text = "$minutesPerDay minutes/jour")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Résumé du planning
                    val daysToComplete = WatchlistSerie(
                        watchlistSerie.serie,
                        episodesPerWeek,
                        episodesPerDay,
                        minutesPerDay
                    ).getDaysToComplete()
                    
                    Text(
                        text = "Avec ces paramètres, vous finirez cette série en $daysToComplete jours",
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Annuler")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            onUpdateSettings(editingIndex, episodesPerDay, episodesPerWeek, minutesPerDay)
                            showEditDialog = false
                        }) {
                            Text("Enregistrer")
                        }
                    }
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
