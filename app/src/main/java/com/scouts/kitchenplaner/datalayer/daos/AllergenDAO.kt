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

package com.scouts.kitchenplaner.datalayer.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.scouts.kitchenplaner.datalayer.dtos.AllergenIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.AllergenPersonIdentifierDTO
import com.scouts.kitchenplaner.datalayer.entities.AllergenEntity
import com.scouts.kitchenplaner.datalayer.entities.AllergenPersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AllergenDAO {
    @Transaction
    suspend fun createAllergensForProject(
        projectId: Long,
        allergens: List<Pair<AllergenPersonEntity, List<AllergenEntity>>>
    ) {
        allergens.forEach {
            it.first.projectId = projectId
            insertAllergenPerson(it.first)
println("")
            it.second.forEach { allergen ->
                allergen.projectId = projectId
                insertAllergen(allergen)
            }
        }
    }

    @Transaction
    suspend fun insertAllergenPersonWithAllergens(
        person: AllergenPersonEntity,
        allergens: List<AllergenEntity>
    ) {
        insertAllergenPerson(person)
        allergens.forEach { allergen ->
            insertAllergen(allergen)
        }
    }

    @Insert
    suspend fun insertAllergenPerson(entity: AllergenPersonEntity) : Long

    @Delete(entity = AllergenPersonEntity::class)
    suspend fun deleteAllergenPerson(entity: AllergenPersonIdentifierDTO)

    @Insert
    suspend fun insertAllergen(entity: AllergenEntity) : Long

    @Delete(entity = AllergenEntity::class)
    suspend fun deleteAllergen(entity: AllergenIdentifierDTO)

    @Query("SELECT * FROM allergenPersons WHERE allergenPersons.projectId = :id")
    fun getAllergenPersonsByProjectID(id: Long) : Flow<List<AllergenPersonEntity>>

    @Query("SELECT * FROM allergens WHERE allergens.projectId = :id")
    fun getAllergensByProjectID(id: Long) : Flow<List<AllergenEntity>>

    @Query("SELECT count(allergen) " +
            "FROM allergens " +
            "WHERE allergens.name = :name " +
            "AND allergens.projectId = :projectId")
    suspend fun getAllergenCountByNameAndProjectId(name: String, projectId: Long) : Int
}