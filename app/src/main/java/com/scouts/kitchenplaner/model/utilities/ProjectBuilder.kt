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

/**
 * Builder class to facilitate easier construction of [Project] objects from network or datalayer
 * entities
 */
class ProjectBuilder {
    private val name: String
    private val id: Long?
    private val startDate: Date
    private val endDate: Date
    private var projectImage: Uri = Uri.EMPTY
    private val isOnline: Boolean
    private var allergenPersons: List<AllergenPerson> = listOf()
    private var mealPlan: MealPlan = MealPlan(Date(0), Date(0))

    /**
     * Initializes a [ProjectBuilder] from the given datalayer entity
     *
     * @param projectEntity The entity the created project should be based on
     */
    constructor(projectEntity: ProjectEntity) {
        this.name = projectEntity.name
        this.id = projectEntity.id
        this.startDate = projectEntity.startDate
        this.endDate = projectEntity.endDate
        this.projectImage = Uri.parse(projectEntity.imageUri)
        this.isOnline = projectEntity.onlineID != null
    }

    /**
     * Initializes a [ProjectBuilder] from the given networklayer dto
     *
     * @param projectDTO The dto the created project should be based on
     */
    constructor(projectDTO: ServerProjectDTO) {
        this.name = projectDTO.name
        this.id = null
        this.startDate = projectDTO.startDate
        this.endDate = projectDTO.endDate
        this.isOnline = true
    }

    /**
     * Use the given data layer entities to set the allergens for the project being created
     *
     * @param allergenPersonEntities The persons that have allergies
     * @param allergenEntities The allergens relevant to the project
     * @return this object for further modification
     */
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

    /**
     * Use the given network layer DTOs to set the allergens for the project being created
     *
     * @param allergenPersons The persons that have allergies
     * @return this object for further modification
     */
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

    /**
     * Sets the meal plan of the project being created from the given data
     *
     * @param meals The meals the project should have
     * @param mainRecipes The main recipes associated to each meal slot
     * @param alternativeRecipes The alternative recipes associated to each meal slot
     * @param numberChanges The number changes at each meal slot
     *
     * @return this object for further modification
     */
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

    /**
     * Selects an image for the project being created
     *
     * @param uri The URI of the image
     * @return this object for further modification
     */
    fun setImageUri(uri: Uri) : ProjectBuilder {
        projectImage = uri
        return this
    }

    /**
     * @return The project that was constructed
     */
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