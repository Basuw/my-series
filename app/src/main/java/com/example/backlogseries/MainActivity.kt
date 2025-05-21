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
    // Liste de séries sauvegardées
    private val watchlist = mutableStateListOf<WatchlistSerie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
                            text = { Text("Ma liste de lecture") }
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
                            // Onglet Ma liste de lecture
                            WatchlistScreen(
                                watchlist = watchlist,
                                onSerieClick = { watchlistSerie ->
                                    navigateToSerieScreen(watchlistSerie.serie)
                                },
                                onRemoveSerie = { watchlistSerie ->
                                    removeFromWatchlist(watchlistSerie)
                                },
                                onUpdateSettings = { index, minutesPerDay ->
                                    updateWatchlistSettings(index, minutesPerDay)
                                }
                            )
                        }
                    }
                }
            }
        }
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
            putExtra("isInWatchlist", watchlist.any { it.serie.id == serie.id })
        }
        startActivityForResult(intent, ADD_TO_WATCHLIST_REQUEST_CODE)
    }

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

    private fun addToWatchlist(serie: Serie) {
        if (watchlist.none { it.serie.id == serie.id }) {
            watchlist.add(WatchlistSerie(serie))
        }
    }

    private fun removeFromWatchlist(watchlistSerie: WatchlistSerie) {
        watchlist.remove(watchlistSerie)
    }

    private fun updateWatchlistSettings(index: Int, minutesPerDay: Int) {
        val updatedSerie = watchlist[index]
        updatedSerie.minutesPerDay = minutesPerDay
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

