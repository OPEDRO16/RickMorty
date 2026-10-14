package com.example.rickmorty.ecras

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickmorty.R
import com.example.rickmorty.network.Character
import com.example.rickmorty.network.RickAndMortyApi
import kotlinx.coroutines.launch

enum class SearchCategory{
    Character, Episode, Location
}

data class DisplayItem(
    val name: String,
    val imageUrl: Any,
    val field1: String,
    val field2: String
)

sealed interface ExplorerUiState {
    data class Success (
        val totalInApi: Int,
        val retrievedCount: Int,
        val currentItem: DisplayItem
    ) : ExplorerUiState

    object Error : ExplorerUiState
    object Loading : ExplorerUiState
}

class RickAndMortyViewModel : ViewModel() {
    var explorerUiState: ExplorerUiState by mutableStateOf(ExplorerUiState.Loading)
        private set

    var currentCategory by mutableStateOf(SearchCategory.Character)
        private set

    private var currentResults: List<DisplayItem> = emptyList()

    init {
        search("")
    }

    fun setCategory(category: SearchCategory){
        currentCategory = category
        search("")
    }

    fun search(nameFilter: String) {
        viewModelScope.launch {
            explorerUiState = ExplorerUiState.Loading
            try {
                val apiService = RickAndMortyApi.retrofitService
                var totalCount = 0

                currentResults = when (currentCategory) {
                    SearchCategory.Character -> {
                        val response = apiService.getCharacter(nameFilter)
                        totalCount = response.info.count
                        response.results.map {
                            DisplayItem(it.name, it.image, "Status: ${it.status}", "Species: ${it.species}")
                        }
                    }

                    SearchCategory.Episode -> {
                        val response = apiService.getEpisode(nameFilter)
                        totalCount = response.info.count
                        response.results.map {
                            DisplayItem(it.name, "https://rickandmortyapi.com/api/character/avatar/19.jpeg", "Air Date: ${it.air_date}", "Episode: ${it.episode}")
                        }
                    }

                    SearchCategory.Location -> {
                        val response = apiService.getLocations(nameFilter)
                        totalCount = response.info.count
                        response.results.map {
                            DisplayItem(it.name, R.drawable.portal, "Type: ${it.type}", "Dimension: ${it.dimension}")
                        }
                    }
                }

                if (currentResults.isNotEmpty()){
                    explorerUiState = ExplorerUiState.Success(totalCount,currentResults.size, currentResults.random())
                } else {
                    explorerUiState = ExplorerUiState.Error
                }
            } catch (e: Exception) {
                explorerUiState = ExplorerUiState.Error
            }
        }
    }

    fun rollRandomItem() {
        if (currentResults.isNotEmpty() && explorerUiState is ExplorerUiState.Success) {
            val currentState = explorerUiState as ExplorerUiState.Success
            explorerUiState = currentState.copy(currentItem = currentResults.random())
        }
    }
}