package com.example.backlogseries.serie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.backlogseries.model.Serie
import com.example.backlogseries.ui.theme.BacklogSeriesTheme

class SerieScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupération de l'objet Serie depuis l'Intent
        val serie = intent.getSerializableExtra("serie") as? Serie
            ?: throw IllegalArgumentException("Serie object is missing")

        setContent {
            BacklogSeriesTheme {
                SerieScreen(serie = serie) // Affichage de l'écran avec les détails de la série
            }
        }
    }
}
