package com.example.backlogseries.serie

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.backlogseries.model.Serie
import com.example.backlogseries.ui.theme.BacklogSeriesTheme

class SerieScreenActivity : ComponentActivity() {
    private lateinit var serie: Serie
    private var isInWatchlist: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupération de l'objet Serie depuis l'Intent
        serie = intent.getSerializableExtra("serie") as? Serie
            ?: throw IllegalArgumentException("Serie object is missing")
            
        isInWatchlist = intent.getBooleanExtra("isInWatchlist", false)

        setContent {
            BacklogSeriesTheme {
                SerieScreen(
                    serie = serie,
                    isInWatchlist = isInWatchlist,
                    onAddToWatchlist = { addToWatchlist() }
                )
            }
        }
    }
    
    private fun addToWatchlist() {
        val resultIntent = Intent().apply {
            putExtra("serie", serie)
            putExtra("addToWatchlist", true)
        }
        setResult(RESULT_OK, resultIntent)
        isInWatchlist = true
        // Rafraîchir l'interface pour mettre à jour le bouton
        setContent {
            BacklogSeriesTheme {
                SerieScreen(
                    serie = serie,
                    isInWatchlist = isInWatchlist,
                    onAddToWatchlist = { addToWatchlist() }
                )
            }
        }
    }
    
    override fun onBackPressed() {
        val resultIntent = Intent().apply {
            putExtra("serie", serie)
            putExtra("addToWatchlist", false) // Ne pas ajouter à la liste si l'utilisateur n'a pas cliqué sur le bouton
        }
        setResult(RESULT_OK, resultIntent)
        super.onBackPressed()
    }
}
