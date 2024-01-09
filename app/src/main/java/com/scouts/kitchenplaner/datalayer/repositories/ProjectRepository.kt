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

package com.scouts.kitchenplaner.datalayer.repositories

import android.net.Uri
import com.scouts.kitchenplaner.datalayer.daos.AllergenDAO
import com.scouts.kitchenplaner.datalayer.daos.ProjectDAO
import com.scouts.kitchenplaner.datalayer.daos.RecipeManagementDAO
import com.scouts.kitchenplaner.datalayer.entities.MealEntity
import com.scouts.kitchenplaner.datalayer.entities.RecipeProjectMealEntity
import com.scouts.kitchenplaner.datalayer.toDataLayerEntity
import com.scouts.kitchenplaner.datalayer.toModelEntity
import com.scouts.kitchenplaner.exceptions.DuplicatePrimaryKeyException
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.ProjectStub
import com.scouts.kitchenplaner.model.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val projectDAO: ProjectDAO,
    private val allergenDAO: AllergenDAO,
    private val recipeManagementDAO: RecipeManagementDAO
) {
    suspend fun insertProject(project: Project) : Long {
        val projectId = projectDAO.createProject(
            project = project.toDataLayerEntity(),
            meals = project.meals.map { MealEntity(it, 0) }
        )
        allergenDAO.createAllergensForProject(
            projectId = projectId,
            allergens = project.allergenPersons.map { it.toDataLayerEntity(project.id) }
        )
        return projectId
    }

    fun getProjectByID(id: Long) : Flow<Project> {
        val projectFlow = projectDAO.getProjectById(id)
        val mealFlow = projectDAO.getMealsByProjectID(id)
        val allergenPersonFlow = allergenDAO.getAllergenPersonsByProjectID(id)
        val allergensFlow = allergenDAO.getAllergensByProjectID(id)
        val personNumberFlow = projectDAO.getPersonNumberChangesByProjectID(id)

        return combine(projectFlow, mealFlow, allergenPersonFlow, allergensFlow, personNumberFlow) { project, meals, allergenPersons, allergens, personNumbers ->
            project.toModelEntity(meals, allergenPersons, allergens, personNumbers)
        }
    }

    @Throws(DuplicatePrimaryKeyException::class)
    suspend fun addMealToProject(id: Long, meal: String) {
        val rowId = projectDAO.insertMealEntity(MealEntity(meal, id))
        if (rowId == -1L) {
            throw DuplicatePrimaryKeyException("meal")
        }
    }

    /**
     * Selects the main recipe for a meal
     */
    suspend fun selectRecipeForProject(projectId: Long, recipeId: Long, meal: String, date: Date) {
        recipeManagementDAO.addRecipeToProjectMeal(
            RecipeProjectMealEntity(projectId, meal, date, recipeId, false)
        )
    }

    /**
     * Testing purposes only, should be deleted once more robust methods of interacting with the
     * database have been established
     */
    suspend fun getProjectByProjectName(projectName: String) : Project {
        val entity = projectDAO.getProjectByProjectName(projectName)
        return Project(entity.id, entity.name, entity.startDate, entity.endDate, listOf(), listOf(), Uri.parse(entity.imageUri))
    }

    fun getProjectOverview(user: User) : Flow<List<ProjectStub>> {
        return projectDAO.getProjectsForUser(user.username).map {
            it.map { project ->
                ProjectStub(project.name, project.id, Uri.parse(project.imageUri))
            }
        }
    }

    fun getAllProjectsOverview() : Flow<List<ProjectStub>> {
        return projectDAO.getAllProjectStubs().distinctUntilChanged { old, new ->
            old.size == new.size && new.containsAll(old)
        }.map {
            it.map { project ->
                ProjectStub(project.name, project.id, Uri.parse(project.imageUri))
            }
        }
    }
}