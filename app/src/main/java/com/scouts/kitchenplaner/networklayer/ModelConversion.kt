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

package com.scouts.kitchenplaner.networklayer

import android.net.Uri
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.model.utilities.ProjectBuilder
import com.scouts.kitchenplaner.networklayer.kitchenplaner.dtos.projects.ServerAllergenPeopleDTO
import com.scouts.kitchenplaner.networklayer.kitchenplaner.dtos.projects.ServerProjectDTO
import com.scouts.kitchenplaner.networklayer.kitchenplaner.dtos.projects.ServerRecipeMappingDTO

fun Project.toNetworkLayerDTO(onlineID: Long): ServerProjectDTO {
    return ServerProjectDTO(
        versionNumber = 0,
        imageVersionNumber = 0,
        name = this.name,
        id = onlineID,
        meals = this.meals,
        startDate = this.startDate,
        endDate = this.endDate,
        allergenPeople = this.allergenPersons.map { it.toNetworkLayerDTO() },
        recipes = this.mealSlots
            .mapNotNull { slot ->
                Pair(slot, this.mealPlan[slot].first ?: return@mapNotNull null)
            }.map { (slot, recipes) ->
                listOf(Triple(slot, recipes.first, true)) + recipes.second.map {
                    Triple(
                        slot,
                        it,
                        false
                    )
                }
            }.flatten()
            .map { (slot, recipe, isMain) ->
                ServerRecipeMappingDTO(
                    date = slot.date,
                    meal = slot.meal,
                    recipeID = recipe.id,
                    mainRecipe = isMain
                )
            },
        unitConversions = listOf(),
        personNumberChange = listOf()
    )
}

fun ServerProjectDTO.toModelEntity(imageUri: Uri, recipeStubs: List<RecipeStub>): Project {
    val mainRecipes = recipes
        .filter { it.mainRecipe }
        .map { dto ->
            Pair(
                MealSlot(dto.date, dto.meal),
                recipeStubs.find { it.id == dto.recipeID }!!
            )
        }

    val alternativeRecipes = recipes
        .filter { !it.mainRecipe }
        .map { dto ->
            Pair(
                MealSlot(dto.date, dto.meal),
                recipeStubs.find { it.id == dto.recipeID }!!
            )
        }

    val numberChanges = personNumberChange.associate { change ->
        MealSlot(change.date, change.meal) to change.differenceBefore
    }

    return ProjectBuilder(this)
        .setAllergenPersonsFromServerDTOs(allergenPeople)
        .setImageUri(imageUri)
        .setMealPlan(meals, mainRecipes, alternativeRecipes, numberChanges)
        .build()
}

fun AllergenPerson.toNetworkLayerDTO(): ServerAllergenPeopleDTO {
    return ServerAllergenPeopleDTO(
        name = this.name,
        arrivalDate = this.arrivalDate,
        departureDate = this.departureDate,
        arrivalMeal = this.arrivalMeal,
        departureMeal = this.departureMeal,
        allergen = this.allergens.filter { !it.traces }.map { it.allergen },
        traces = this.allergens.filter { it.traces }.map { it.allergen }
    )
}