package com.example.backlogseries.serie

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
                model = "https://image.tmdb.org/t/p/w500${serie.backdropPath}",
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
                text = "⭐ ${serie.voteAverage} (${serie.voteCount})",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }

        // Genres
        Text(
            text = "Genres: ${serie.genres.joinToString(", ") { it.name }}",
            fontSize = 16.sp,
            color = Color.Gray
        )

        // Date de sortie
        Text(
            text = "Première diffusion : ${serie.firstAirDate}",
            fontSize = 16.sp,
            color = Color.Gray
        )

        // Nombre de saisons et d'épisodes
        Text(
            text = "Saisons : ${serie.numberOfSeasons}, Épisodes : ${serie.numberOfEpisodes}",
            fontSize = 16.sp,
            color = Color.Gray
        )

        // Temps de visionnage total
        val totalRuntime = serie.getTotalRuntime()
        if (totalRuntime != null) {
            Text(
                text = "Temps de visionnage total : ${totalRuntime / 60}h ${totalRuntime % 60}min",
                fontSize = 16.sp,
                color = Color.Gray
            )
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
