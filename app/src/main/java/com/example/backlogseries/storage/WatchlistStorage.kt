package com.example.backlogseries.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import com.example.backlogseries.model.Serie
import com.example.backlogseries.model.WatchlistSerie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WatchlistStorage(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveWatchlist(watchlist: List<WatchlistSerie>) {
        val json = gson.toJson(watchlist)
        sharedPreferences.edit().putString(KEY_WATCHLIST, json).apply()
    }

    fun loadWatchlist(): List<WatchlistSerie> {
        val json = sharedPreferences.getString(KEY_WATCHLIST, null) ?: return emptyList()
        val type = object : TypeToken<List<WatchlistSerie>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addSerieToWatchlist(watchlist: MutableList<WatchlistSerie>, serie: Serie): Boolean {
        // Vérifier si la série est déjà dans la liste
        if (watchlist.any { it.serie.id == serie.id }) {
            return false
        }
        
        // Ajouter la série à la liste
        val watchlistSerie = WatchlistSerie(serie)
        watchlist.add(watchlistSerie)
        
        // Sauvegarder la liste mise à jour
        saveWatchlist(watchlist)
        return true
    }

    fun removeSerieFromWatchlist(watchlist: MutableList<WatchlistSerie>, serieId: Int): Boolean {
        val initialSize = watchlist.size
        watchlist.removeAll { it.serie.id == serieId }
        
        // Si la taille a changé, on a supprimé une série
        if (watchlist.size != initialSize) {
            saveWatchlist(watchlist)
            return true
        }
        return false
    }

    fun updateWatchlistSettings(
        watchlist: MutableList<WatchlistSerie>,
        index: Int, 
        episodesPerDay: Int, 
        episodesPerWeek: Int, 
        minutesPerDay: Int
    ): Boolean {
        if (index in watchlist.indices) {
            watchlist[index] = watchlist[index].copy(
                episodesPerDay = episodesPerDay,
                episodesPerWeek = episodesPerWeek,
                minutesPerDay = minutesPerDay
            )
            saveWatchlist(watchlist)
            return true
        }
        return false
    }

    companion object {
        private const val PREFS_NAME = "watchlist_preferences"
        private const val KEY_WATCHLIST = "watchlist_data"
    }
}
