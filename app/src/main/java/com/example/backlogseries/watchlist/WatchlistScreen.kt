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
                            onUpdateSettings(index, watchlistSerie.episodesPerDay, watchlistSerie.episodesPerWeek, watchlistSerie.minutesPerDay)
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
