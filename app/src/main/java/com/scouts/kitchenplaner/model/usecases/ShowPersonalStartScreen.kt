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
import com.scouts.kitchenplaner.repositories.ProjectRepository
import com.scouts.kitchenplaner.repositories.RecipeRepository
import com.scouts.kitchenplaner.model.entities.ProjectStub
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.model.entities.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val AMOUNT_PROJECTS = 3
private const val AMOUNT_RECIPES = 3

/**
 * Use case for displaying a personal start screen consisting of short cuts to the most recently
 * seen recipes and projects by the current user
 *
 * @param projectRepository Repository for retrieving data about the last shown projects
 * @param recipeRepository Repository for retrieving data about the last shown recipes
 * @param dataStore Access for getting the current user
 */
class ShowPersonalStartScreen @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val recipeRepository: RecipeRepository,
    private val dataStore: KitchenAppDataStore
) {


    /**
     * Provides the latest used projects for the current user.
     * The amount of projects is determent by [AMOUNT_PROJECTS]
     *
     * @return A flow containing stubs for the requested projects
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLatestProjectsForCurrentUser(): Flow<List<ProjectStub>> {
        var currentUser: User
        runBlocking {
            currentUser = dataStore.getCurrentUser()
        }
        return projectRepository.getLastShownProjectIds(currentUser, AMOUNT_PROJECTS)
            .flatMapLatest { ids ->
                val helper = ids.map { id ->
                    projectRepository.getProjectMetaDataByID(id).map { it.stub }
                }
                combine(helper) {
                    it.toList()
                }
            }
    }

    /**
     * Provides the latest used recipes for the current user.
     * The amount of recipes is determent by [AMOUNT_RECIPES]
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLatestRecipesForCurrentUser(): Flow<List<RecipeStub>> {
        var currentUser: User;
        runBlocking {
            currentUser = dataStore.getCurrentUser()
        }
        return recipeRepository.getLastShownRecipeIdsForUser(
            user = currentUser, limit = AMOUNT_RECIPES
        ).flatMapLatest { ids ->
            val helper = ids.map { id ->
                recipeRepository.getRecipeStubById(id)
            }
            combine(helper) { it.toList() }
        }
    }
}