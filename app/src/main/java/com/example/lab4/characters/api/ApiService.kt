package com.example.lab4.characters.api

import com.example.lab4.characters.model.Character
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api/characters")
    suspend fun getCharacters(@Query("page") page: Int, @Query("pageSize") pageSize: Int): List<Character>
}