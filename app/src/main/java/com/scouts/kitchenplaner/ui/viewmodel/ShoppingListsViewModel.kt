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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.ShoppingListStub
import com.scouts.kitchenplaner.model.usecases.DisplayShoppingListOverview
import com.scouts.kitchenplaner.model.usecases.EditShoppingLists
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingListsViewModel @Inject constructor(
    private val displayShoppingListOverview: DisplayShoppingListOverview,
    private val editShoppingLists: EditShoppingLists
) : ViewModel() {
    fun getShoppingLists(project: Project) : StateFlow<List<ShoppingListStub>> {
        return displayShoppingListOverview
            .getShoppingListStubsForProject(project)
            .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())
    }

    fun deleteShoppingList(project: Project, list: ShoppingListStub) {
        viewModelScope.launch {
            editShoppingLists.deleteShoppingList(project, list)
        }
    }
}