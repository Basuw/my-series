package com.example.backlogseries

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.backlogseries.model.Serie
import com.example.backlogseries.serie.SerieScreenActivity
import com.example.backlogseries.serie.TopRatedSeriesScreen
import com.example.backlogseries.service.SerieService
import com.example.backlogseries.ui.theme.BacklogSeriesTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BacklogSeriesTheme {
                val serieService = SerieService(this)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TopRatedSeriesScreen(
                        serieService = serieService,
                        onSerieClick = { serie ->
                            fetchAndNavigateToSerieDetails(serieService, serie)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
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
        }
        startActivity(intent)
    }
}
