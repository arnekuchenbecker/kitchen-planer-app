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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import com.scouts.kitchenplaner.model.entities.ProjectStub
import com.scouts.kitchenplaner.model.usecases.ProjectSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProjectSelectionViewModel @Inject constructor(
    private val projectSelection: ProjectSelection
) : ViewModel() {
    private val projects = projectSelection.getProjectsForCurrentUser()
    private val selectedFlow = MutableStateFlow<Map<Long, Boolean>>(mapOf())

    val projectSelected: Flow<List<Pair<ProjectStub, Boolean>>> =
        projects.combine(selectedFlow) { project, selected ->
            project.map { stub ->
                Pair(
                    stub,
                    selected[stub.id] ?: false
                )
            }
        }
    var archive by mutableStateOf(false)
    var showArchiveDialog by mutableStateOf(false)



    fun toggleSelection(projectId: Long, selected: Boolean) {
        selectedFlow.update{
            it + Pair(projectId,selected)
        }
    }

    fun clearSelection(){
        selectedFlow.update {mutableStateMapOf() }
    }
}
