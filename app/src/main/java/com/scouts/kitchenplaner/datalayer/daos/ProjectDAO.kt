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

package com.scouts.kitchenplaner.datalayer.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Transaction
import com.scouts.kitchenplaner.datalayer.entities.AllergenEntity
import com.scouts.kitchenplaner.datalayer.entities.AllergenPersonEntity
import com.scouts.kitchenplaner.datalayer.entities.MealEntity
import com.scouts.kitchenplaner.datalayer.entities.ProjectEntity

@Dao
interface ProjectDAO {
    @Transaction
    suspend fun createProject(
        project: ProjectEntity,
        meals: List<MealEntity>,
        allergens: List<Pair<AllergenPersonEntity, List<AllergenEntity>>>
    ) : Long {
        val projectId = insertProject(project)

        meals.forEach {
            it.projectId = projectId
            insertMealEntity(it)
        }

        allergens.forEach {
            it.first.projectId = projectId
            insertAllergenPerson(it.first)

            it.second.forEach { allergen ->
                allergen.projectId = projectId
                insertAllergen(allergen)
            }
        }

        return projectId
    }

    @Insert
    suspend fun insertProject(entity: ProjectEntity) : Long

    @Insert
    suspend fun insertAllergenPerson(entity: AllergenPersonEntity) : Long

    @Insert
    suspend fun insertMealEntity(entity: MealEntity) : Long

    @Insert
    suspend fun insertAllergen(entity: AllergenEntity) : Long
}
