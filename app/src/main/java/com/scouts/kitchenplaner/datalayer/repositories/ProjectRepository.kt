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
import com.scouts.kitchenplaner.datalayer.daos.ShoppingListDAO
import com.scouts.kitchenplaner.datalayer.dtos.MealIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.PersonNumberChangeIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectArchivedDTO
import com.scouts.kitchenplaner.datalayer.entities.MealEntity
import com.scouts.kitchenplaner.datalayer.entities.PersonNumberChangeEntity
import com.scouts.kitchenplaner.datalayer.entities.UserProjectEntity
import com.scouts.kitchenplaner.datalayer.toDataLayerEntity
import com.scouts.kitchenplaner.datalayer.toModelEntity
import com.scouts.kitchenplaner.exceptions.DuplicatePrimaryKeyException
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.ProjectMetaData
import com.scouts.kitchenplaner.model.entities.ProjectStub
import com.scouts.kitchenplaner.model.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val projectDAO: ProjectDAO,
    private val allergenDAO: AllergenDAO,
    private val recipeManagementDAO: RecipeManagementDAO,
    private val shoppingListDAO: ShoppingListDAO
) {
    suspend fun insertProject(project: Project, creator: User) : Long {
        val projectId = projectDAO.createProject(
            project = project.toDataLayerEntity(),
            meals = project.meals.mapIndexed { index, it -> MealEntity(it, index, 0) }
        )
        allergenDAO.createAllergensForProject(
            projectId = projectId,
            allergens = project.allergenPersons.map { it.toDataLayerEntity(project.id) }
        )
        projectDAO.addUserToProject(UserProjectEntity(projectId, creator.username))
        return projectId
    }

    fun getProjectMetaDataByID(id: Long) : Flow<ProjectMetaData> {
        return projectDAO.getProjectById(id).map { it.toModelEntity() }
    }

    fun getMealsByProjectID(id: Long) : Flow<List<String>> {
        return projectDAO.getMealsByProjectID(id)
    }

    fun getPersonNumberChangesByProjectID(id: Long) : Flow<Map<MealSlot, Int>> {
        return projectDAO.getPersonNumberChangesByProjectID(id).map {
            val changeMap = mutableMapOf<MealSlot, Int>()
            changeMap.putAll(it.map { change ->
                Pair(MealSlot(change.date, change.meal), change.difference)
            })
            changeMap
        }
    }

    suspend fun setPersonNumberChange(id: Long, meal: String, date: Date, difference: Int) {
        projectDAO.insertPersonNumberChange(PersonNumberChangeEntity(id, date, meal, difference))
    }

    suspend fun removePersonNumberChange(id: Long, meal: String, date: Date) {
        projectDAO.deletePersonNumberChange(PersonNumberChangeIdentifierDTO(id, meal, date))
    }

    @Throws(DuplicatePrimaryKeyException::class)
    suspend fun addMealToProject(meal: String, index: Int, projectId: Long) {
        projectDAO.increaseMealOrder(projectId, index)
        val rowId = projectDAO.insertMealEntity(MealEntity(meal, index, projectId))
        if (rowId == -1L) {
            throw DuplicatePrimaryKeyException("meal")
        }
    }

    suspend fun deleteMealFromProject(meal: String, projectId: Long) {
        val order = projectDAO.getMealOrder(projectId, meal)
        projectDAO.deleteMeal(MealIdentifierDTO(projectId, meal))
        projectDAO.decreaseMealOrder(projectId, order)
    }

    /**
     * Testing purposes only, should be deleted once more robust methods of interacting with the
     * database have been established
     */
    suspend fun getProjectByProjectName(projectName: String) : ProjectMetaData {
        val entity = projectDAO.getProjectByProjectName(projectName)
        return entity.toModelEntity()
    }

    fun getProjectOverview(user: User) : Flow<List<ProjectStub>> {
        return projectDAO.getProjectsForUser(user.username).map {
            it.map { project ->
                ProjectStub(project.name, project.id, Uri.parse(project.imageUri))
            }
        }
    }

    /**
     * Testing purposes only
     */
    fun getAllProjectsOverview() : Flow<List<ProjectStub>> {
        return projectDAO.getAllProjectStubs().distinctUntilChanged { old, new ->
            old.size == new.size && new.containsAll(old)
        }.map {
            it.map { project ->
                ProjectStub(project.name, project.id, Uri.parse(project.imageUri))
            }
        }
    }

    suspend fun leaveProject(user: User, projectId: Long) {
        projectDAO.removeUserFromProject(UserProjectEntity(projectId, user.username))
    }

    suspend fun archiveProject(projectId: Long) {
        recipeManagementDAO.archiveProjectRecipes(projectId)
        allergenDAO.deleteAllergenPersonsForProject(projectId)
        projectDAO.deleteMealsByProjectId(projectId)
        shoppingListDAO.deleteShoppingListsByProjectId(projectId)
        projectDAO.setProjectArchivedStatus(ProjectArchivedDTO(projectId, true))
    }
}