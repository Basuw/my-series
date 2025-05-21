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
        val editor = sharedPreferences.edit()
        val json = gson.toJson(watchlist.map {
            mapOf(
                "serie" to it.serie,
                "minutesPerDay" to it.minutesPerDay
            )
        })
        editor.putString(KEY_WATCHLIST, json)
        editor.apply()
    }

    fun loadWatchlist(): List<WatchlistSerie> {
        val json = sharedPreferences.getString(KEY_WATCHLIST, null) ?: return emptyList()
        val listType = object : TypeToken<List<Map<String, Any>>>() {}.type
        val rawList: List<Map<String, Any>> = gson.fromJson(json, listType)
        return rawList.map {
            WatchlistSerie(
                serie = gson.fromJson(gson.toJson(it["serie"]), Serie::class.java),
                minutesPerDay = (it["minutesPerDay"] as Double).toInt()
            )
        }
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

    companion object {
        private const val PREFS_NAME = "watchlist_preferences"
        private const val KEY_WATCHLIST = "watchlist_data"
    }
}
