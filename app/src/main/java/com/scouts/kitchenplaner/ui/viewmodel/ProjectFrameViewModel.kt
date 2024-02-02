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

package com.scouts.kitchenplaner.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.entities.ProjectStub
import com.scouts.kitchenplaner.model.usecases.EditProjectSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectFrameViewModel @Inject constructor(
    private val projectSettings: EditProjectSettings
) : ViewModel() {
    fun getProjectStub(projectId: Long) : StateFlow<ProjectStub> {
        return projectSettings.getProjectStub(projectId).stateIn(viewModelScope, SharingStarted.Eagerly, ProjectStub("", projectId, Uri.EMPTY))
    }

    fun setProjectName(projectId: Long, name: String) {
        viewModelScope.launch {
            projectSettings.setProjectName(projectId, name)
        }
    }

    fun setProjectImage(projectId: Long, image: Uri) {
        viewModelScope.launch {
            projectSettings.setProjectPicture(projectId, image)
        }
    }
}