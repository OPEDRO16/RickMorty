package com.example.rickmorty.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://rickandmortyapi.com/api/"

interface RickAndMortyApiService {
    @GET("character")
    suspend fun getCharacter(
        @Query("name") name: String
    ): CharacterResponse

    @GET("episode")
    suspend fun getEpisode(
        @Query("name") name: String
    ): EpisodeResponse

    @GET("location")
    suspend fun getLocations(
        @Query("name") name: String
    ): LocationResponse
}

private val json = Json { ignoreUnknownKeys = true }

val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

object RickAndMortyApi {
    val retrofitService: RickAndMortyApiService by lazy {
        retrofit.create(RickAndMortyApiService::class.java)
    }
}