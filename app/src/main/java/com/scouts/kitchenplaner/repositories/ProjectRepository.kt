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

package com.scouts.kitchenplaner.repositories

import android.net.Uri
import com.scouts.kitchenplaner.datalayer.daos.AllergenDAO
import com.scouts.kitchenplaner.datalayer.daos.ProjectDAO
import com.scouts.kitchenplaner.datalayer.daos.RecipeDAO
import com.scouts.kitchenplaner.datalayer.daos.RecipeManagementDAO
import com.scouts.kitchenplaner.datalayer.daos.ShoppingListDAO
import com.scouts.kitchenplaner.datalayer.dtos.MealIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.PersonNumberChangeIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectArchivedDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectDataVersionDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectDatesDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectIdDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectImageDTO
import com.scouts.kitchenplaner.datalayer.entities.MealEntity
import com.scouts.kitchenplaner.datalayer.entities.PersonNumberChangeEntity
import com.scouts.kitchenplaner.datalayer.entities.UserEntity
import com.scouts.kitchenplaner.datalayer.entities.UserProjectEntity
import com.scouts.kitchenplaner.datalayer.toDataLayerEntity
import com.scouts.kitchenplaner.datalayer.toModelEntity
import com.scouts.kitchenplaner.exceptions.DuplicatePrimaryKeyException
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.ProjectMetaData
import com.scouts.kitchenplaner.model.entities.ProjectStub
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.model.entities.User
import com.scouts.kitchenplaner.model.utilities.ProjectBuilder
import com.scouts.kitchenplaner.networklayer.kitchenplaner.services.ProjectAPIService
import com.scouts.kitchenplaner.networklayer.toModelEntity
import com.scouts.kitchenplaner.networklayer.toNetworkLayerDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val projectDAO: ProjectDAO,
    private val allergenDAO: AllergenDAO,
    private val recipeManagementDAO: RecipeManagementDAO,
    private val shoppingListDAO: ShoppingListDAO,
    private val recipeDAO: RecipeDAO,
    private val projectAPIService: ProjectAPIService
) {
    suspend fun insertProject(project: Project, creator: User): Long {
        val projectId = projectDAO.createProject(
            project = project.toDataLayerEntity(),
            meals = project.meals.mapIndexed { index, it -> MealEntity(it, index, 0) }
        )
        allergenDAO.createAllergensForProject(
            projectId = projectId,
            allergens = project.allergenPersons.map { it.toDataLayerEntity(project.id) }
        )
        if (!existsUser(creator)) { // TODO - should this really be how it works?
            projectDAO.insertUser(UserEntity(creator.username))
        }
        projectDAO.addUserToProject(UserProjectEntity(projectId, creator.username, Date()))
        return projectId
    }

    fun getProjectMetaDataByID(id: Long): Flow<ProjectMetaData> {
        return projectDAO.getProjectById(id).map { it.toModelEntity() }
    }

    fun getProjectStubByID(id: Long): Flow<ProjectStub> {
        return projectDAO.getProjectById(id)
            .map { ProjectStub(it.name, it.id, Uri.parse(it.imageUri)) }
    }

    fun getMealsByProjectID(id: Long): Flow<List<String>> {
        return projectDAO.getMealsByProjectID(id)
    }

    suspend fun changeProjectPicture(id: Long, imageUri: Uri) {
        projectDAO.updateImage(ProjectImageDTO(id, imageUri.toString()))
    }

    fun getPersonNumberChangesByProjectID(id: Long): Flow<Map<MealSlot, Int>> {
        return projectDAO.getPersonNumberChangesByProjectID(id).map {
            val changeMap = mutableMapOf<MealSlot, Int>()
            changeMap.putAll(it.map { change ->
                Pair(
                    MealSlot(change.date, change.meal),
                    change.differenceBefore
                )
            })
            changeMap
        }
    }

    suspend fun setPersonNumberChange(id: Long, meal: String, date: Date, differenceBefore: Int) {
        projectDAO.insertPersonNumberChange(
            PersonNumberChangeEntity(
                id,
                date,
                meal,
                differenceBefore
            )
        )
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
    suspend fun getProjectByProjectName(projectName: String): ProjectMetaData {
        val entity = projectDAO.getProjectByProjectName(projectName)
        return entity.toModelEntity()
    }

    fun getProjectOverview(user: User): Flow<List<ProjectStub>> {
        return projectDAO.getProjectsForUser(user.username).map {
            it.map { project ->
                ProjectStub(project.name, project.id, Uri.parse(project.imageUri))
            }
        }
    }

    /**
     * Testing purposes only
     */
    fun getAllProjectsOverview(): Flow<List<ProjectStub>> {
        return projectDAO.getAllProjectStubs().distinctUntilChanged { old, new ->
            old.size == new.size && new.containsAll(old)
        }.map {
            it.map { project ->
                ProjectStub(project.name, project.id, Uri.parse(project.imageUri))
            }
        }
    }

    suspend fun setProjectName(id: Long, name: String) {
        projectDAO.changeProjectName(id, name)
    }

    suspend fun setProjectDates(id: Long, startDate: Date, endDate: Date) {
        projectDAO.updateDates(ProjectDatesDTO(id, startDate, endDate))
    }

    suspend fun leaveProject(user: User, projectId: Long) {
        projectDAO.removeUserFromProject(UserProjectEntity(projectId, user.username, Date()))
    }

    suspend fun archiveProject(projectId: Long) {
        recipeManagementDAO.archiveProjectRecipes(projectId)
        allergenDAO.deleteAllergenPersonsForProject(ProjectIdDTO(projectId))
        projectDAO.deleteMealsByProjectId(ProjectIdDTO(projectId))
        shoppingListDAO.deleteShoppingListsByProjectId(ProjectIdDTO(projectId))
        projectDAO.setProjectArchivedStatus(ProjectArchivedDTO(projectId, true))
    }

    // TODO has to be used every time a user sees a project
    suspend fun updateProjectShown(projectId: Long, user: User, time: Date) {
        projectDAO.updateLastShownProjectForUser(UserProjectEntity(projectId, user.username, time))
    }

    fun getLastShownProjectIds(user: User, limit: Int): Flow<List<Long>> {
        val projects = projectDAO.getLatestShownProjectsForUser(user.username, limit)
            .map {
                it.map { project ->
                    project.projectId
                }
            }
        return projects
    }

    /**
     * Publishes a project to the server that has not yet been published
     *
     * @param projectID The ID of the project that should be published
     */
    suspend fun publishProject(projectID: Long) {
        val project = getCurrentProject(projectID)
        if (project.isOnline) {
            return
        }
        val onlineID = projectAPIService.createNewProject(project.toNetworkLayerDTO(0))
        projectDAO.initializeOnlineProject(projectID, onlineID)
    }

    /**
     * Tries to update the project with the given ID. On success, the version number in the data
     * base is updated.
     *
     * @param projectID The ID of the project to update
     */
    suspend fun pushProjectUpdate(projectID: Long) {
        val project = getCurrentProject(projectID)
        if (!project.isOnline) {
            return
        }
        val onlineID = projectDAO.getCurrentOnlineIDByProjectID(projectID)
        val response = projectAPIService.updateProject(onlineID, project.toNetworkLayerDTO(onlineID))
        if (response.isSuccessful) {
            val newVersion = response.body()!!
            projectDAO.updateVersionNumber(ProjectDataVersionDTO(projectID, newVersion))
        } else {
            println("Updating the project failed with error code ${response.code()}: ${response.message()}")
        }
    }

    /**
     * Pulls updated data for all projects the given user is part of from the server
     *
     * @param user The user for who to update the projects
     */
    suspend fun getUpdatedProjects(user: User) {
        val stubs = projectAPIService.getProjectStubsByUsername(user.username)

        stubs.forEach { stub ->
            if (projectDAO.existsProjectByOnlineID(stub.id) && !projectDAO.isProjectArchived(stub.id)) {
                if (stub.imageVersion > projectDAO.getCurrentImageVersionNumberByID(stub.id)) {
                    //TODO update image
                }
                if (stub.projectVersion > projectDAO.getCurrentProjectVersionNumberByID(stub.id)) {
                    val project = projectAPIService.getProject(stub.id)
                    val id = projectDAO.getCurrentProjectIDByOnlineID(stub.id)
                    val imageUri = Uri.parse(projectDAO.getCurrentImageURIByID(stub.id))
                    val recipeStubs = /*TODO get recipe stubs for project from server*/ listOf<RecipeStub>()
                    projectDAO.deleteProject(id)
                    insertProject(project.toModelEntity(imageUri, recipeStubs), user)
                }
            }
        }
    }

    private suspend fun existsUser(user: User): Boolean {
        return projectDAO.getExistsUserByName(user.username) == 1
    }

    private suspend fun getCurrentProject(id: Long) : Project {
        val projectEntity = projectDAO.getCurrentProjectById(id)

        val allergenPersons = allergenDAO.getCurrentAllergenPersonsByProjectID(id)
        val allergens = allergenDAO.getCurrentAllergensByProjectID(id)

        val meals = projectDAO.getCurrentMealsByProjectID(id)
        val mainRecipes = recipeManagementDAO.getCurrentMainRecipesForProject(id)
        val alternativeRecipes = recipeManagementDAO.getCurrentAlternativeRecipesForProject(id)
        val recipeIDs = mainRecipes.map { it.recipeId } + alternativeRecipes.map { it.recipeId }
        val recipes = recipeDAO.getCurrentRecipeStubsByIDs(recipeIDs).map {
            RecipeStub(
                it.id,
                it.title,
                Uri.parse(it.imageURI)
            )
        }
        val numberChanges = projectDAO.getCurrentPersonNumberChangesByProjectID(id)

        val mainRecipeMappings = mainRecipes.map { entity ->
            Pair(MealSlot(entity.date, entity.meal), recipes.find { it.id == entity.recipeId }!!)
        }

        val alternativeRecipeMappings = alternativeRecipes.map { entity ->
            Pair(MealSlot(entity.date, entity.meal), recipes.find { it.id == entity.recipeId }!!)
        }

        return ProjectBuilder(projectEntity)
            .setAllergenPersonsFromEntities(allergenPersons, allergens)
            .setMealPlan(
                meals,
                mainRecipeMappings,
                alternativeRecipeMappings,
                numberChanges.associate { MealSlot(it.date, it.meal) to it.differenceBefore }
            )
            .build()
    }
}