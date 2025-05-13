package com.example.backlogseries.serie

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.backlogseries.model.Serie
import com.example.backlogseries.model.getTotalRuntime

@Composable
fun SerieScreen(serie: Serie) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Image de la série
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${serie.backdrop_path}",
                contentDescription = "Backdrop Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Titre et note
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = serie.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "⭐ ${serie.vote_average} (${serie.vote_count})",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        // Genres
        val genresText = if (!serie.genres.isNullOrEmpty()) {
            "Genres: ${serie.genres.joinToString(", ") { it.name }}"
        } else {
            "Genres: Non disponible"
        }
        Text(
            text = genresText,
            fontSize = 16.sp,
            color = Color.Gray
        )

        // Date de sortie
        Text(
            text = "Première diffusion : ${serie.first_air_date}",
            fontSize = 16.sp,
            color = Color.Gray
        )

        // Nombre de saisons et d'épisodes
        Text(
            text = "Saisons : ${serie.number_of_seasons}, Épisodes : ${serie.number_of_episodes}",
            fontSize = 16.sp,
            color = Color.Gray
        )

        // Temps de visionnage total
        var calculated = remember { mutableStateOf(false) }
        val totalRuntime = serie.getTotalRuntime(calculated)
        if (totalRuntime != null) {
            Text(
                text = "Temps de visionnage total : ${totalRuntime / 60}h ${totalRuntime % 60}min",
                fontSize = 16.sp,
                color = Color.Gray
            )
            if (calculated.value) {
                Text(
                    text = "(calculé à partir de la durée du dernier épisode)",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
        // Synopsis
        Text(
            text = serie.overview,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
