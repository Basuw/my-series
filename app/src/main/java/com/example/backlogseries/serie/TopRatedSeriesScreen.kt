package com.example.backlogseries.serie

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.backlogseries.model.Serie
import com.example.backlogseries.service.SerieService


@Composable
fun TopRatedSeriesScreen(
    serieService: SerieService,
    onSerieClick: (Serie) -> Unit,
    modifier: Modifier = Modifier // Ajout du paramètre modifier
) {
    val series = remember { mutableStateOf<List<Serie>>(emptyList()) }

    Column {
        LaunchedEffect(Unit) {
            series.value = serieService.getTopRatedSeries().results
        }
        // Affichage de la liste des séries
        Text(text = "Top Rated Series", fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))

        LazyColumn(modifier = modifier.fillMaxSize()) { // Utilisation du modifier ici
            items(series.value) { serie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onSerieClick(serie) }
                ) {
                    Row(modifier = Modifier.padding(8.dp)) {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w200${serie.poster_path}",
                            contentDescription = "Poster",
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = serie.name, fontWeight = FontWeight.Bold)
                            Text(text = "⭐ ${serie.vote_average}")
                        }
                    }
                }
            }
        }
    }
}
