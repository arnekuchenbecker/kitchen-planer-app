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

import android.content.Context
import androidx.room.Room
import com.scouts.kitchenplaner.datalayer.KitchenAppDataStore
import com.scouts.kitchenplaner.datalayer.KitchenAppDatabase
import com.scouts.kitchenplaner.datalayer.daos.AllergenDAO
import com.scouts.kitchenplaner.datalayer.daos.ProjectDAO
import com.scouts.kitchenplaner.datalayer.daos.RecipeDAO
import com.scouts.kitchenplaner.datalayer.daos.RecipeManagementDAO
import com.scouts.kitchenplaner.datalayer.daos.ShoppingListDAO
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
    fun provideProjectDao(database: KitchenAppDatabase) : ProjectDAO {
        return database.projectDao()
    }

    @Provides
    fun provideShoppingListDao(database: KitchenAppDatabase) : ShoppingListDAO {
        return database.shoppingListDao()
    }

    @Provides
    fun provideRecipeDao(database: KitchenAppDatabase): RecipeDAO {
        return database.recipeDao()
    }

    @Provides
    fun provideAllergenDao(database: KitchenAppDatabase) : AllergenDAO {
        return database.allergenDao()
    }

    @Provides
    fun provideRecipeManagementDao(database: KitchenAppDatabase) : RecipeManagementDAO {
        return database.recipeManagementDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext application: Context): KitchenAppDatabase {
        return Room
            .databaseBuilder(application, KitchenAppDatabase::class.java, "hordentopf.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext application: Context) : KitchenAppDataStore {
        return KitchenAppDataStore(application)
    }
}