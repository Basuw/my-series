package com.example.backlogseries

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.backlogseries.model.Serie
import com.example.backlogseries.serie.SerieScreenActivity
import com.example.backlogseries.serie.TopRatedSeriesScreen
import com.example.backlogseries.service.SerieService
import com.example.backlogseries.ui.theme.BacklogSeriesTheme

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
                            navigateToSerieScreen(serie)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
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
