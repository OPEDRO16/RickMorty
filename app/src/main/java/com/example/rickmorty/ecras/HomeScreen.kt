package com.example.rickmorty.ecras

import android.R
import android.graphics.ImageDecoder
import android.os.Build
import android.widget.Button
import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.rickmorty.network.Character

@Composable
fun HomeScreen(viewModel: RickAndMortyViewModel) {
    val uiState = viewModel.explorerUiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Text("Select Category", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SearchCategory.values().forEach { category ->
                Button(
                    onClick = {
                        viewModel.setCategory(category)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.currentCategory == category)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(category.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var searchText by remember { mutableStateOf("") }
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                viewModel.search(it)
            },
            label = { Text("Filter Character by name...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is ExplorerUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            ExplorerUiState.Error -> {
                Text("Nenhum resultado encontrado.", color = MaterialTheme.colorScheme.error)
            }
            is ExplorerUiState.Success -> {
                ResultScreen(
                    totalInApi = uiState.totalInApi,
                    retrievedCount = uiState.retrievedCount,
                    item = uiState.currentItem,
                    onRollClick = { viewModel.rollRandomItem() }
                )
            }
        }
    }
}

@Composable
fun ResultScreen(
    totalInApi: Int,
    retrievedCount: Int,
    item: DisplayItem,
    onRollClick: () -> Unit
){
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Api Stats", style = MaterialTheme.typography.titleMedium)
                Text("Total in API: $totalInApi")
                Text("Retrieved in current view: $retrievedCount items")
            }
        }

        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    imageLoader = imageLoader,
                    contentDescription = item.name,
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = item.name, style = MaterialTheme.typography.headlineSmall)

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    Text(text = item.field1)
                    Text(text = item.field2)
                }
            }
        }

        Button(onClick = onRollClick, modifier = Modifier.fillMaxWidth()) {
            Text("Roll Random Item")
        }
    }
}