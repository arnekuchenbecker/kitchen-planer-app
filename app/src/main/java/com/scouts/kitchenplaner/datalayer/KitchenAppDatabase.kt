/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023-2024 Arne Kuchenbecker, Antonia Heiming
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

package com.scouts.kitchenplaner.datalayer

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.scouts.kitchenplaner.datalayer.daos.AllergenDAO
import com.scouts.kitchenplaner.datalayer.daos.ProjectDAO
import com.scouts.kitchenplaner.datalayer.daos.RecipeDAO
import com.scouts.kitchenplaner.datalayer.daos.RecipeManagementDAO
import com.scouts.kitchenplaner.datalayer.daos.ShoppingListDAO
import com.scouts.kitchenplaner.datalayer.entities.AllergenEntity
import com.scouts.kitchenplaner.datalayer.entities.AllergenPersonEntity
import com.scouts.kitchenplaner.datalayer.entities.AlternativeRecipeProjectMealEntity
import com.scouts.kitchenplaner.datalayer.entities.DietarySpeciality
import com.scouts.kitchenplaner.datalayer.entities.IngredientEntity
import com.scouts.kitchenplaner.datalayer.entities.IngredientGroupEntity
import com.scouts.kitchenplaner.datalayer.entities.InstructionEntity
import com.scouts.kitchenplaner.datalayer.entities.MainRecipeProjectMealEntity
import com.scouts.kitchenplaner.datalayer.entities.MealEntity
import com.scouts.kitchenplaner.datalayer.entities.PersonNumberChangeEntity
import com.scouts.kitchenplaner.datalayer.entities.ProjectEntity
import com.scouts.kitchenplaner.datalayer.entities.RecipeEntity
import com.scouts.kitchenplaner.datalayer.entities.ShoppingListEntity
import com.scouts.kitchenplaner.datalayer.entities.ShoppingListEntryEntity
import com.scouts.kitchenplaner.datalayer.entities.UserEntity
import com.scouts.kitchenplaner.datalayer.entities.UserProjectEntity
import com.scouts.kitchenplaner.datalayer.typeconverters.DateConverter
import com.scouts.kitchenplaner.datalayer.typeconverters.DietaryTypeConverter

@Database(
    entities = [
        ProjectEntity::class,
        AllergenPersonEntity::class,
        MealEntity::class,
        AllergenEntity::class,
        UserEntity::class,
        UserProjectEntity::class,
        DietarySpeciality::class,
        RecipeEntity::class,
        IngredientEntity::class,
        IngredientGroupEntity::class,
        InstructionEntity::class,
        MainRecipeProjectMealEntity::class,
        AlternativeRecipeProjectMealEntity::class,
        PersonNumberChangeEntity::class,
        ShoppingListEntity::class,
        ShoppingListEntryEntity::class
    ],
    version = 1
)
@TypeConverters(DateConverter::class, DietaryTypeConverter::class)
abstract class KitchenAppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDAO
    abstract fun recipeDao(): RecipeDAO
    abstract fun allergenDao(): AllergenDAO
    abstract fun recipeManagementDao(): RecipeManagementDAO
    abstract fun shoppingListDao(): ShoppingListDAO
}