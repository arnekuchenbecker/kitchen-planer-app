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

package com.scouts.kitchenplaner.model.usecases.authentication

import com.scouts.kitchenplaner.networklayer.AuthService
import com.scouts.kitchenplaner.networklayer.dtos.AuthenticationDTO
import java.security.KeyStore
import javax.inject.Inject

class Authentication @Inject constructor(private val authService: AuthService) {
    val keystore: KeyStore =  KeyStore.getInstance(KeyStore.getDefaultType())

    suspend fun register(user: String, password: String): Boolean{
        val response = authService.register(AuthenticationDTO(user,password))
        return response.isSuccessful
    }

    suspend fun login(user:String, password:String): Boolean{
        val response = authService.login(AuthenticationDTO(user,password))
        if(!response.isSuccessful){
            return false;
        }
        val token = response.body()!!
    return false

        
    }
}