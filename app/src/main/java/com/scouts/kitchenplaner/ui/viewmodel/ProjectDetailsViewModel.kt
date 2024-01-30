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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenCheck
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.model.usecases.CheckAllergens
import com.scouts.kitchenplaner.model.usecases.EditMealPlan
import com.scouts.kitchenplaner.model.usecases.EditProjectPicture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ProjectDetailsViewModel @Inject constructor(
    private val checkAllergens: CheckAllergens,
    private val editMealPlan: EditMealPlan,
    private val editProjectPicture: EditProjectPicture
) : ViewModel() {
    lateinit var projectFlow: StateFlow<Project>

    var recipeQuery by mutableStateOf("")
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    var recipeSuggestions = snapshotFlow { recipeQuery }.flatMapLatest {
        println("Calling flow for $it")
        editMealPlan.findRecipesForQuery(it)
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = listOf())

    @OptIn(DomainLayerRestricted::class)
    suspend fun getProject(projectId: Long) {
        delay(2000)
        projectFlow = flowOf(
            Project(
                _id = projectId,
                _name = "Dummy Project asdöflkjaskdöfalkds",
                initialStartDate = Date(0),
                initialEndDate = Date(48 * 3600 * 1000),
                _allergenPersons = listOf(
                    AllergenPerson(
                        name = "Test",
                        allergens = listOf(Allergen("Gluten", true)),
                        arrivalDate = Date(0),
                        arrivalMeal = "Frühstück",
                        departureDate = Date(48 * 3600 * 1000),
                        departureMeal = "Frühstück"
                    )
                ),
                initialMeals = listOf("Frühstück", "Mittagessen"),
                _projectImage = Uri.parse("content://com.android.providers.media.documents/document/image%3A62")
            ).apply {
                mealPlan.setPlan(
                    mapOf(
                        MealSlot(Date(0), "Frühstück") to Pair(
                            RecipeStub(1, "Frühstück", Uri.EMPTY),
                            listOf(
                                RecipeStub(2, "Frühstück (glutenspuren)", Uri.EMPTY),
                            )
                        ),
                        MealSlot(Date(24*3600*1000), "Frühstück") to Pair(
                            RecipeStub(4, "Unknown", Uri.EMPTY),
                            listOf()
                        ),
                        MealSlot(Date(48*3600*1000), "Frühstück") to Pair(
                            RecipeStub(1, "Frühstück", Uri.EMPTY),
                            listOf(
                                RecipeStub(3, "Frühstück (glutenfrei)", Uri.EMPTY)
                            )
                        )
                    )
                )
            }
        ).stateIn(viewModelScope)
    }

    fun getAllergenCheck(slot: MealSlot): StateFlow<AllergenCheck> {
        return checkAllergens.getAllergenCheck(projectFlow, slot).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AllergenCheck()
        )
    }

    fun swapMeals(project: Project, first: MealSlot, second: MealSlot) {
        viewModelScope.launch {
            editMealPlan.swapMealSlots(project, first, second)
        }
    }

    fun onDeleteMainRecipe(project: Project, slot: MealSlot) {
        viewModelScope.launch {
            editMealPlan.removeMainRecipeFromMeal(project, slot)
        }
    }

    fun onDeleteAlternativeRecipe(project: Project, slot: MealSlot, recipeStub: RecipeStub) {
        viewModelScope.launch {
            editMealPlan.removeAlternativeRecipeFromMeal(project, slot, recipeStub)
        }
    }

    fun onRecipeQueryChanged(newQuery: String) {
        recipeQuery = newQuery
    }

    fun exchangeRecipe(project: Project, mealSlot: MealSlot, oldRecipe: RecipeStub, newID: Long) {
        viewModelScope.launch {
            if (project.mealPlan[mealSlot].first?.first?.id == oldRecipe.id) {
                editMealPlan.removeMainRecipeFromMeal(project, mealSlot)
                editMealPlan.selectMainRecipeForMealSlot(project, mealSlot, newID)
            } else {
                editMealPlan.removeAlternativeRecipeFromMeal(project, mealSlot, oldRecipe)
                editMealPlan.addAlternativeRecipeForMealSlot(project, mealSlot, newID)
            }
        }
    }

    fun addRecipe(project: Project, mealSlot: MealSlot, newID: Long) {
        viewModelScope.launch {
            if (project.mealPlan[mealSlot].first != null) {
                editMealPlan.addAlternativeRecipeForMealSlot(project, mealSlot, newID)
            } else {
                editMealPlan.selectMainRecipeForMealSlot(project, mealSlot, newID)
            }
        }
    }

    fun setProjectImage(project: Project, path: Uri?) {
        viewModelScope.launch {
            editProjectPicture.setProjectPicture(project, path ?: return@launch)
        }
    }
}