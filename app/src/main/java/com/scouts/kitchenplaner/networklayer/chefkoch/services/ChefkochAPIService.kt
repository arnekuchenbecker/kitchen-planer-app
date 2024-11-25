/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023-2024 Arne Kuchenbecker, Antonia Heiming, Anton Kadelbach, Sandra Lanz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.scouts.kitchenplaner.networklayer.chefkoch.services

import com.scouts.kitchenplaner.networklayer.chefkoch.dtos.ChefkochRecipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * retrofit service for making requests to chefkoch's API
 */
interface ChefkochAPIService {
    @GET("recipes/{id}")
    suspend fun getRecipe(@Path("id") recipeId: Long) : Response<ChefkochRecipe>
}