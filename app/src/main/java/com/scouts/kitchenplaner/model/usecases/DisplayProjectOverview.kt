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

import com.scouts.kitchenplaner.datalayer.repositories.AllergenRepository
import com.scouts.kitchenplaner.datalayer.repositories.ProjectRepository
import com.scouts.kitchenplaner.datalayer.repositories.RecipeManagementRepository
import com.scouts.kitchenplaner.datalayer.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.model.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DisplayProjectOverview @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val allergenRepository: AllergenRepository,
    private val recipeManagementRepository: RecipeManagementRepository,
    private val recipeRepository: RecipeRepository
) {
    @OptIn(DomainLayerRestricted::class)
    fun getProject(projectId: Long) : Flow<Project> {
        val initialProject = runBlocking {
            projectRepository.getProjectMetaDataByID(projectId).map {
                Project(
                    it.stub.id,
                    it.stub.name,
                    it.startDate,
                    it.endDate,
                    mutableListOf(),
                    mutableListOf(),
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
                project.setID(metaData.stub.id)
                project.setName(metaData.stub.name)
                project.setStartDate(metaData.startDate)
                project.setEndDate(metaData.endDate)
                project.setImageUri(metaData.stub.imageUri)
                project
            }
            .combine(allergenPersonFlow) { project, allergenPersons ->
                project.setAllergenPersons(allergenPersons)
                project
            }
            .combine(mealFlow) { project, meals ->
                project.mealPlan.setMeals(meals)
                project
            }
            .combine(mealPlanFlow) { project, mealPlan ->
                project.mealPlan.setPlan(mealPlan)
                project
            }
            .combine(numberChangeFlow) { project, numberChanges ->
                project.mealPlan.setNumberChanges(numberChanges)
                project
            }
    }

    suspend fun archiveProject(project: Project) {
        projectRepository.archiveProject(project.id)
    }

    suspend fun leaveProject(project: Project) {
        val currentUser = User("Arne") // TODO - get current user from datastore
        projectRepository.leaveProject(currentUser, project.id)
    }
}