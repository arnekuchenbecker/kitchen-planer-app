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

package com.scouts.kitchenplaner.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.usecases.JoinProject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model for joining a project
 *
 * @param joinProject Use case for performing the joining process
 */
@HiltViewModel
class JoinProjectViewModel @Inject constructor(
    private val joinProject: JoinProject
) : ViewModel() {
    /**
     * Whether the join was completed
     */
    var done by mutableStateOf(false)

    /**
     * Have the current user join the project with the given ID
     *
     * @param projectID The online ID of the project to be joined
     */
    fun joinProject(projectID: Long) {
        viewModelScope.launch {
            joinProject.joinProject(projectID)
            done = true
        }
    }
}