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

package com.scouts.kitchenplaner.model.usecases

import com.scouts.kitchenplaner.datalayer.KitchenAppDataStore
import com.scouts.kitchenplaner.datalayer.repositories.AllergenRepository
import com.scouts.kitchenplaner.datalayer.repositories.ProjectRepository
import com.scouts.kitchenplaner.datalayer.repositories.RecipeManagementRepository
import com.scouts.kitchenplaner.datalayer.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import com.scouts.kitchenplaner.model.entities.MealPlan
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import java.util.Date
import javax.inject.Inject

/**
 * Use case to display a project from a given user and leave it.
 *
 * @param projectRepository Repository for accessing information about the project
 * @param allergenRepository Repository for accessing information about all allergens present in the project
 * @param recipeRepository Repository that provides information about the management of recipes within the project
 * @param recipeManagementRepository Repository for retrieving data of recipes
 * @param userRepository Repository for retrieving the current user
 */
class DisplayProjectOverview @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val allergenRepository: AllergenRepository,
    private val recipeManagementRepository: RecipeManagementRepository,
    private val recipeRepository: RecipeRepository,
    private val userRepository: KitchenAppDataStore
) {

    /**
     * Provides the requested project
     *
     * @param projectId The id of the project
     * @return A flow of the requested project
     */
    @OptIn(DomainLayerRestricted::class)
    fun getProject(projectId: Long): Flow<Project> {
        val initialProject = runBlocking {
            projectRepository.getProjectMetaDataByID(projectId).map {
                Project(
                    it.stub.id,
                    it.stub.name,
                    mutableListOf(),
                    MealPlan(it.startDate, it.endDate, mutableListOf()),
                    it.stub.imageUri
                )
            }.first()
        }
        val metaDataFlow = projectRepository.getProjectMetaDataByID(projectId)
        val mealFlow = projectRepository.getMealsByProjectID(projectId)
        val allergenPersonFlow = allergenRepository.getAllergenPersonsByProjectID(projectId)
        val mealSlotFlow = recipeManagementRepository.getRecipeMealSlotsForProject(projectId)
        val recipeFlow = recipeRepository.getRecipeStubsByProjectId(projectId)
        val mealPlanFlow = mealSlotFlow.combine(recipeFlow) { meals, recipes ->
            val mealMap = mutableMapOf<MealSlot, Pair<RecipeStub, List<RecipeStub>>>()
            meals.forEach { (slot, mealRecipes) ->
                val mainRecipe = recipes.find { it.id == mealRecipes.first }
                val alternatives = recipes.filter { mealRecipes.second.contains(it.id) }
                if (mainRecipe != null) {
                    mealMap[slot] = Pair(mainRecipe, alternatives)
                }
            }
            mealMap
        }
        val numberChangeFlow = projectRepository.getPersonNumberChangesByProjectID(projectId)

        return flowOf(initialProject)
            .combine(metaDataFlow) { project, metaData ->
                project.withMetaData(metaData)
            }
            .combine(allergenPersonFlow) { project, allergenPersons ->
                project.withAllergenPersons(allergenPersons)
            }
            .combine(mealFlow) { project, meals ->
                project.withMeals(meals)
            }
            .combine(mealPlanFlow) { project, mealPlan ->
                project.withMealPlan(mealPlan)
            }
            .combine(numberChangeFlow) { project, numberChanges ->
                project.withNumberChanges(numberChanges)
            }.onStart {
                projectRepository.updateProjectShown(
                    projectId, userRepository.getCurrentUser(),
                    Date()
                )
            }
    }

    /**
     * Archives a project. Only the meta data is present on the device, if it  is an online project.
     * If it is an offline project it gets deleted.
     *
     * @param project The project to archive
     */
    suspend fun archiveProject(project: Project) {
        projectRepository.archiveProject(project.id)
    }

    /**
     * The current user leaves the online project. If the project is offline, the project gets deleted
     *
     * @param project The project to leave
     */
    suspend fun leaveProject(project: Project) {
        val currentUser = userRepository.getCurrentUser()
        projectRepository.leaveProject(currentUser, project.id)
    }
}