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
import com.scouts.kitchenplaner.model.entities.Project
import javax.inject.Inject

/**
 * Use case for creating a new project for a user
 *
 * @param projectRepository Repository for storing the project
 * @param userRepository Repository for retrieving information about the current user
 */
class CreateProject @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val userRepository: KitchenAppDataStore
) {
    /**
     * Creates a new project
     *
     * @param project The project to be created
     *
     * @return The project id of the newly created project or null if project wasn't a valid project.
     */
    suspend fun createProject(
        project: Project
    ): Long? {
        /*
        * TODO: Check that project is actually valid to be a new project, i.e:
        *       - There shouldn't be any recipes added yet
        *       - There shouldn't be any shopping lists created yet
        * */
        if (project.endDate.before(project.startDate)) {
            return null
        }
        val currentUser = userRepository.getCurrentUser()
        return projectRepository.insertProject(project, currentUser)
    }
}