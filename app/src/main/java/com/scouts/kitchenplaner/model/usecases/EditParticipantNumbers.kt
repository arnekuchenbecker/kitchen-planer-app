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

package com.scouts.kitchenplaner.model.usecases

import com.scouts.kitchenplaner.datalayer.repositories.ProjectRepository
import com.scouts.kitchenplaner.model.entities.MealNumberChange
import com.scouts.kitchenplaner.model.entities.Project
import java.util.Date
import javax.inject.Inject

class EditParticipantNumbers @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend fun setPersonNumberChange(project: Project, meal: String, date: Date, numberChange: MealNumberChange) {
        if (numberChange.before == 0 && numberChange.after == 0) {
            removePersonNumberChange(project, meal, date)
        }
        projectRepository.setPersonNumberChange(project.id, meal, date, numberChange)
    }

    suspend fun removePersonNumberChange(project: Project, meal: String, date: Date) {
        projectRepository.removePersonNumberChange(project.id, meal, date)
    }
}