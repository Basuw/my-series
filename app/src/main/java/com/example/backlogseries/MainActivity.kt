package com.example.backlogseries

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.backlogseries.model.Serie
import com.example.backlogseries.model.WatchlistSerie
import com.example.backlogseries.serie.SerieScreenActivity
import com.example.backlogseries.serie.TopRatedSeriesScreen
import com.example.backlogseries.service.SerieService
import com.example.backlogseries.storage.WatchlistStorage
import com.example.backlogseries.ui.theme.BacklogSeriesTheme
import com.example.backlogseries.watchlist.WatchlistScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    // Liste de lecture partagée entre les activités
    private val watchlist = mutableStateListOf<WatchlistSerie>()
    // Gestionnaire de stockage pour la liste de lecture
    private lateinit var watchlistStorage: WatchlistStorage
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialiser le gestionnaire de stockage et charger la liste
        watchlistStorage = WatchlistStorage(this)
        loadWatchlist()
        
        setContent {
            BacklogSeriesTheme {
                val serieService = SerieService(this)
                var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
                var searchResults by remember { mutableStateOf<List<Serie>>(emptyList()) }
                var selectedTab by remember { mutableStateOf(0) }
                
                Column(modifier = Modifier.padding(10.dp)) {
                    // Navigation par onglets
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Découvrir") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Liste de lecture") }
                        )
                    }
                    
                    // Contenu basé sur l'onglet sélectionné
                    when (selectedTab) {
                        0 -> {
                            // Onglet Découvrir
                            // Barre de recherche
                            SearchBar(
                                query = searchQuery,
                                onQueryChanged = { query ->
                                    searchQuery = query
                                    if (query.text.isNotEmpty()) {
                                        lifecycleScope.launch {
                                            delay(300) // Ajout d'un délai pour éviter les appels excessifs
                                            searchResults = serieService.searchSeries(query.text)
                                        }
                                    } else {
                                        searchResults = emptyList()
                                    }
                                }
                            )

                            // Affichage des résultats de recherche ou des séries les mieux notées
                            if (searchQuery.text.isNotEmpty()) {
                                TopRatedSeriesScreen(
                                    series = searchResults,
                                    onSerieClick = { serie ->
                                        fetchAndNavigateToSerieDetails(serieService, serie)
                                    }
                                )
                            } else {
                                TopRatedSeriesScreen(
                                    serieService = serieService,
                                    onSerieClick = { serie ->
                                        fetchAndNavigateToSerieDetails(serieService, serie)
                                    }
                                )
                            }
                        }
                        1 -> {
                            // Onglet Liste de lecture
                            WatchlistScreen(
                                watchlist = watchlist,
                                onSerieClick = { watchlistSerie ->
                                    navigateToSerieScreen(watchlistSerie.serie)
                                },
                                onRemoveSerie = { watchlistSerie ->
                                    removeFromWatchlist(watchlistSerie)
                                },
                                onUpdateSettings = { index, episodesPerDay, episodesPerWeek, minutesPerDay ->
                                    updateWatchlistSettings(index, episodesPerDay, episodesPerWeek, minutesPerDay)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Charger la liste de lecture depuis le stockage
    private fun loadWatchlist() {
        watchlist.clear()
        watchlist.addAll(watchlistStorage.loadWatchlist())
        Log.d("MainActivity", "Liste de lecture chargée: ${watchlist.size} séries")
    }
    
    // Ajouter une série à la liste de lecture
    private fun addToWatchlist(serie: Serie): Boolean {
        val result = watchlistStorage.addSerieToWatchlist(watchlist, serie)
        Log.d("MainActivity", "Ajout à la liste de lecture: $result")
        return result
    }
    
    // Supprimer une série de la liste de lecture
    private fun removeFromWatchlist(watchlistSerie: WatchlistSerie) {
        watchlistStorage.removeSerieFromWatchlist(watchlist, watchlistSerie.serie.id)
        Log.d("MainActivity", "Série supprimée de la liste de lecture")
    }
    
    // Mettre à jour les paramètres d'une série dans la liste de lecture
    private fun updateWatchlistSettings(index: Int, episodesPerDay: Int, episodesPerWeek: Int, minutesPerDay: Int) {
        watchlistStorage.updateWatchlistSettings(watchlist, index, episodesPerDay, episodesPerWeek, minutesPerDay)
        Log.d("MainActivity", "Paramètres de visionnage mis à jour")
    }

    private fun fetchAndNavigateToSerieDetails(serieService: SerieService, serie: Serie) {
        lifecycleScope.launch {
            try {
                val updatedSerie = serieService.getSerieDetails(serie.id) // Appel API pour récupérer les détails
                Log.d("MainActivity", "Fetched serie details: $updatedSerie")
                navigateToSerieScreen(updatedSerie) // Navigation avec la série mise à jour
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun navigateToSerieScreen(serie: Serie) {
        val intent = Intent(this, SerieScreenActivity::class.java).apply {
            putExtra("serie", serie)
            // Check if serie is already in watchlist
            val isInWatchlist = watchlist.any { it.serie.id == serie.id }
            putExtra("isInWatchlist", isInWatchlist)
            putExtra("watchlistSize", watchlist.size)
        }
        startActivityForResult(intent, ADD_TO_WATCHLIST_REQUEST_CODE)
    }
    
    // Méthode pour récupérer le résultat de SerieScreenActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TO_WATCHLIST_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val serie = data.getSerializableExtra("serie") as? Serie
            val shouldAddToWatchlist = data.getBooleanExtra("addToWatchlist", false)
            
            if (serie != null && shouldAddToWatchlist) {
                addToWatchlist(serie)
            }
        }
    }
    
    companion object {
        const val ADD_TO_WATCHLIST_REQUEST_CODE = 100
    }
}

@Composable
fun SearchBar(query: TextFieldValue, onQueryChanged: (TextFieldValue) -> Unit) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                if (query.text.isEmpty()) {
                    Text(text = "Rechercher une série...", color = Color.Gray)
                }
                innerTextField()
            }
        }
    )
}

