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

package com.scouts.kitchenplaner.model.utilities

import android.net.Uri
import com.scouts.kitchenplaner.datalayer.entities.AllergenEntity
import com.scouts.kitchenplaner.datalayer.entities.AllergenPersonEntity
import com.scouts.kitchenplaner.datalayer.entities.ProjectEntity
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.MealPlan
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.networklayer.kitchenplaner.dtos.projects.ServerAllergenPeopleDTO
import com.scouts.kitchenplaner.networklayer.kitchenplaner.dtos.projects.ServerProjectDTO
import java.util.Date

class ProjectBuilder {
    private val name: String
    private val id: Long?
    private val startDate: Date
    private val endDate: Date
    private var projectImage: Uri = Uri.EMPTY
    private val isOnline: Boolean
    private var allergenPersons: List<AllergenPerson> = listOf()
    private var mealPlan: MealPlan = MealPlan(Date(0), Date(0))

    constructor(projectEntity: ProjectEntity) {
        this.name = projectEntity.name
        this.id = projectEntity.id
        this.startDate = projectEntity.startDate
        this.endDate = projectEntity.endDate
        this.projectImage = Uri.parse(projectEntity.imageUri)
        this.isOnline = projectEntity.onlineID != null
    }

    constructor(projectDTO: ServerProjectDTO) {
        this.name = projectDTO.name
        this.id = null
        this.startDate = projectDTO.startDate
        this.endDate = projectDTO.endDate
        this.isOnline = true
    }

    fun setAllergenPersonsFromEntities(
        allergenPersonEntities: List<AllergenPersonEntity>,
        allergenEntities: List<AllergenEntity>
    ) : ProjectBuilder {
        allergenPersons = allergenPersonEntities.map { entity ->
            val allergens = allergenEntities
                .filter { it.name == entity.name }
                .map { Allergen(it.allergen, it.traces) }
            AllergenPerson(
                name = entity.name,
                allergens = allergens,
                arrivalDate = entity.arrivalDate,
                arrivalMeal = entity.arrivalMeal,
                departureDate = entity.departureDate,
                departureMeal = entity.departureMeal
            )
        }

        return this
    }

    fun setAllergenPersonsFromServerDTOs(allergenPersons: List<ServerAllergenPeopleDTO>) : ProjectBuilder {
        this.allergenPersons = allergenPersons.map { person ->
            AllergenPerson(
                name = person.name,
                allergens = person.allergen.map { Allergen(it, false) }
                        + person.traces.map { Allergen(it, true) },
                arrivalDate = person.arrivalDate,
                arrivalMeal = person.arrivalMeal,
                departureDate = person.departureDate,
                departureMeal = person.departureMeal
            )
        }

        return this
    }

    fun setMealPlan(
        meals: List<String>,
        mainRecipes: List<Pair<MealSlot, RecipeStub>>,
        alternativeRecipes: List<Pair<MealSlot, RecipeStub>>,
        numberChanges: Map<MealSlot, Int>
    ) : ProjectBuilder {
        val mainRecipesByMealSlot = mainRecipes
            .groupBy { it.first }
            .filter { (_, recipes) -> recipes.isNotEmpty() }
            .mapValues { (_, recipes) -> recipes[0].second }

        val alternativeRecipesByMealSlot = alternativeRecipes
            .groupBy { it.first }
            .filter { (_, recipes) -> recipes.isNotEmpty() }
            .mapValues { (_, recipes) ->
                recipes.map { recipe -> recipe.second }
            }

        val mealPlanMap = mainRecipesByMealSlot.keys
            .filter { alternativeRecipesByMealSlot.contains(it) }
            .associateWith { slot ->
                Pair(mainRecipesByMealSlot[slot]!!, alternativeRecipesByMealSlot[slot]!!)
            }

        mealPlan = MealPlan(
            _startDate = startDate,
            _endDate = endDate,
            initialMeals = meals,
            initialPlan = mealPlanMap,
            initialNumberChanges = numberChanges
        )

        return this
    }

    fun setImageUri(uri: Uri) : ProjectBuilder {
        projectImage = uri
        return this
    }

    fun build() : Project {
        return Project(
            _id = this.id,
            _name = this.name,
            _mealPlan = this.mealPlan,
            _projectImage = this.projectImage,
            _allergenPersons = this.allergenPersons,
            _isOnline = this.isOnline
        )
    }
}