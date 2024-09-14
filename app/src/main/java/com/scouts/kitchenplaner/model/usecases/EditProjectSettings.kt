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

import android.net.Uri
import com.scouts.kitchenplaner.datalayer.repositories.ProjectRepository
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import java.util.Date
import javax.inject.Inject

/**
 * Use case for editing the meta data from a project
 *
 * @param projectRepository Repository for archiving data about the project from the data base
 * and save the changes
 */
class EditProjectSettings @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    /**
     * Updates the name of the project
     *
     * @param project The project to update the name
     * @param name The new name for the project
     */
    suspend fun setProjectName(project: Project, name: String) {
        projectRepository.setProjectName(project.id, name)
    }

    /**
     * Updates the picture of the project
     *
     * @param project The project to update the picture
     * @param image The new URI to the new image
     */
    suspend fun setProjectPicture(project: Project, image: Uri) {
        projectRepository.changeProjectPicture(project.id, image)
    }

    /**
     * Updates the start and end date of the project
     *
     * @param project The project to update the dates
     * @param startDate The new start date of the project
     * @param endDate The new end date of the project
     */
    suspend fun setProjectDates(project: Project, startDate: Date, endDate: Date) {
        if (startDate.before(endDate)) {
            projectRepository.setProjectDates(project.id, startDate, endDate)
        }
    }

    /**
     * Updates the how many people arrive/leave before meal slots
     *
     * @param project The project in which the change happens
     * @param changes all new changes, which contains before which meal slot arrive/leave how many people
     */
    suspend fun setNumberChanges(project: Project, changes: Map<MealSlot, Int>) {
        changes.forEach { (slot, change) ->
            val result = project.mealSlots
                .filter { it.before(slot, project.meals) }
                .fold(0) { acc, newValue ->
                    acc + (changes[newValue] ?: 0)
                }
            if (result >= 0) {
                projectRepository.setPersonNumberChange(project.id, slot.meal, slot.date, change)
            }
        }
    }
}