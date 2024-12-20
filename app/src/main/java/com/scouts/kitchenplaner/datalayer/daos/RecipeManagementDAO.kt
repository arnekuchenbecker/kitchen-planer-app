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

package com.scouts.kitchenplaner.datalayer.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.scouts.kitchenplaner.datalayer.dtos.ProjectIdDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectMealIdentifier
import com.scouts.kitchenplaner.datalayer.entities.AlternativeRecipeProjectMealEntity
import com.scouts.kitchenplaner.datalayer.entities.MainRecipeProjectMealEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface RecipeManagementDAO {
    @Transaction
    suspend fun removeAllRecipesFromMeal(projectId: Long, meal: String, date: Date) {
        removeMainRecipeFromProjectMeal(ProjectMealIdentifier(projectId, meal, date))
        removeAllAlternativeRecipesFromMeal(ProjectMealIdentifier(projectId, meal, date))
    }

    @Transaction
    suspend fun swapMeals(projectId: Long, firstMeal: String, firstDate: Date, secondMeal: String, secondDate: Date) {
        val firstRecipe = getMainRecipeIdForMealSlotImmediate(projectId, firstMeal, firstDate)
        val secondRecipe = getMainRecipeIdForMealSlotImmediate(projectId, secondMeal, secondDate)
        val firstAlternatives = getAlternativeRecipeIdsForMealSlotImmediate(projectId, firstMeal, firstDate)
        val secondAlternatives = getAlternativeRecipeIdsForMealSlotImmediate(projectId, secondMeal, secondDate)

        removeAllRecipesFromMeal(projectId, firstMeal, firstDate)
        removeAllRecipesFromMeal(projectId, secondMeal, secondDate)

        if (secondRecipe != null) {
            addMainRecipeToProjectMeal(MainRecipeProjectMealEntity(projectId, firstMeal, firstDate, secondRecipe))
        }
        addAllAlternativeRecipesToProjectMeal(secondAlternatives.map {
            AlternativeRecipeProjectMealEntity(projectId, firstMeal, firstDate, it)
        })

        if (firstRecipe != null) {
            addMainRecipeToProjectMeal(MainRecipeProjectMealEntity(projectId, secondMeal, secondDate, firstRecipe))
        }
        addAllAlternativeRecipesToProjectMeal(firstAlternatives.map {
            AlternativeRecipeProjectMealEntity(projectId, secondMeal, secondDate, it)
        })
    }

    @Transaction
    suspend fun archiveProjectRecipes(id: Long) {
        deleteAlternativeRecipesByProjectId(ProjectIdDTO(id))
        deleteMainRecipesByProjectId(ProjectIdDTO(id))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMainRecipeToProjectMeal(entity: MainRecipeProjectMealEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSingleAlternativeRecipeToProjectMeal(entity: AlternativeRecipeProjectMealEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAllAlternativeRecipesToProjectMeal(entities: List<AlternativeRecipeProjectMealEntity>)

    @Delete(MainRecipeProjectMealEntity::class)
    suspend fun removeMainRecipeFromProjectMeal(entity: ProjectMealIdentifier)

    @Delete(AlternativeRecipeProjectMealEntity::class)
    suspend fun removeAllAlternativeRecipesFromMeal(entity: ProjectMealIdentifier)

    @Delete
    suspend fun removeSingleAlternativeRecipeFromMeal(entity: AlternativeRecipeProjectMealEntity)

    @Query("SELECT recipeId FROM recipeProjectMeal " +
            "WHERE projectId = :projectId " +
            "AND meal = :meal " +
            "AND date = :date")
    suspend fun getMainRecipeIdForMealSlotImmediate(projectId: Long, meal: String, date: Date) : Long?

    @Query("SELECT recipeId FROM alternativeRecipeProjectMeal " +
            "WHERE projectId = :projectId " +
            "AND meal = :meal " +
            "AND date = :date")
    suspend fun getAlternativeRecipeIdsForMealSlotImmediate(projectId: Long, meal: String, date: Date) : List<Long>

    @Query("SELECT recipeId FROM recipeProjectMeal " +
            "WHERE projectId = :projectId " +
            "AND meal = :meal " +
            "AND date = :date")
    fun getMainRecipeIdForMealSlot(projectId: Long, meal: String, date: Date) : Flow<Long>

    @Query("SELECT recipeId FROM alternativeRecipeProjectMeal " +
            "WHERE projectId = :projectId " +
            "AND meal = :meal " +
            "AND date = :date")
    fun getAlternativeRecipeIdsForMealSlot(projectId: Long, meal: String, date: Date) : Flow<List<Long>>

    @Query("SELECT * FROM recipeProjectMeal WHERE projectId = :projectId")
    fun getMainRecipesForProject(projectId: Long) : Flow<List<MainRecipeProjectMealEntity>>

    /**
     * Gets all main recipe mappings for a project
     *
     * @param projectId The ID of the project
     * @return A list containing all main recipe mappings for that project
     */
    @Query("SELECT * FROM recipeProjectMeal WHERE projectId = :projectId")
    suspend fun getCurrentMainRecipesForProject(projectId: Long) : List<MainRecipeProjectMealEntity>

    @Query("SELECT * FROM alternativeRecipeProjectMeal WHERE projectId = :projectId")
    fun getAlternativeRecipesForProject(projectId: Long) : Flow<List<AlternativeRecipeProjectMealEntity>>

    /**
     * Gets all alternative recipe mappings for a project
     *
     * @param projectId The ID of the project
     * @return A list containing all alternative recipe mappings for that project
     */
    @Query("SELECT * FROM alternativeRecipeProjectMeal WHERE projectId = :projectId")
    suspend fun getCurrentAlternativeRecipesForProject(projectId: Long) : List<AlternativeRecipeProjectMealEntity>

    // Methods for archiving projects
    @Delete(AlternativeRecipeProjectMealEntity::class)
    fun deleteAlternativeRecipesByProjectId(projectId: ProjectIdDTO)

    @Delete(MainRecipeProjectMealEntity::class)
    fun deleteMainRecipesByProjectId(projectId: ProjectIdDTO)
}