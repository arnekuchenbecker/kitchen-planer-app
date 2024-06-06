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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.shoppinglists.ShoppingListStub
import com.scouts.kitchenplaner.model.usecases.DisplayShoppingListOverview
import com.scouts.kitchenplaner.model.usecases.EditShoppingLists
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This view model provides functions to work with the shopping list overview and edit them.
 * @param displayShoppingListOverview  use case that provides stubs of shopping list for a specified project
 * @param editShoppingLists use case that provides methods to delete shopping lists
 */
@HiltViewModel
class ShoppingListsOverviewViewModel @Inject constructor(
    private val displayShoppingListOverview: DisplayShoppingListOverview,
    private val editShoppingLists: EditShoppingLists
) : ViewModel() {
    /**
     * Provides all shopping lists that belong to the given project
     * @param project The project for which the shopping lists are requested
     * @return a flow of all requested shopping lists
     */
    fun getShoppingLists(project: Project) : StateFlow<List<ShoppingListStub>> {
        return displayShoppingListOverview
            .getShoppingListStubsForProject(project)
            .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())
    }

    /**
     * deletes a specified shopping list for a project
     * @param project The project which contains the shopping list
     * @param list The shopping list to be deleted
     */
    fun deleteShoppingList(project: Project, list: ShoppingListStub) {
        viewModelScope.launch {
            editShoppingLists.deleteShoppingList(project, list)
        }
    }
}