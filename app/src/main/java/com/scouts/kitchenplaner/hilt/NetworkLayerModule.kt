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

package com.scouts.kitchenplaner.hilt

import com.scouts.kitchenplaner.TestImageService
import com.scouts.kitchenplaner.networklayer.CHEFKOCH_BASE_URL
import com.scouts.kitchenplaner.networklayer.ChefkochAPIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkLayerModule {
    @Provides
    @Singleton
    fun provideChefkochRetrofitClient() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(CHEFKOCH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideChefkochAPIService(retrofit: Retrofit) : ChefkochAPIService {
        return retrofit.create(ChefkochAPIService::class.java)
    }

    @Provides
    fun provideTestService() : TestImageService {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TestImageService::class.java)
    }
}