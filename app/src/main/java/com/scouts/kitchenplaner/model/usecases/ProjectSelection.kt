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
import com.scouts.kitchenplaner.model.entities.ProjectStub
import com.scouts.kitchenplaner.model.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Use case to display all projects from a user
 * @param projectRepository Repository providing information about the projects
 * @param userRepository Repository providing information about the current user
 */
class ProjectSelection @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val userRepository: KitchenAppDataStore
) {

    /**
     * Provides all projects in which the current user is part in
     *
     * @return A flow containing all project stubs of the requested projects
     */
    fun getProjectsForCurrentUser(): Flow<List<ProjectStub>> {
        val currentUser: User
        runBlocking {
            currentUser = userRepository.getCurrentUser()
        }
        return projectRepository.getProjectOverview(currentUser)
    }
    //TODO delete project
}