/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023  Arne Kuchenbecker, Antonia Heiming
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

import android.content.Context
import androidx.room.Room
import com.scouts.kitchenplaner.datalayer.KitchenAppDatabase
import com.scouts.kitchenplaner.datalayer.daos.ProjectDAO
import com.scouts.kitchenplaner.datalayer.daos.RecipeDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DataLayerModule {
    @Provides
    fun projectDao(database: KitchenAppDatabase): ProjectDAO {
        return database.projectDao()
    }

    @Provides
    fun recipeDao(database: KitchenAppDatabase): RecipeDAO {
        return database.recipeDao()
    }

    @Provides
    @Singleton
    fun myDatabase(@ApplicationContext application: Context): KitchenAppDatabase {
        return Room
            .databaseBuilder(application, KitchenAppDatabase::class.java, "hordentopf.db")
            .build()
    }
}