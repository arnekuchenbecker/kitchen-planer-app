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

package com.scouts.kitchenplaner.model.entities

import android.net.Uri
import com.scouts.kitchenplaner.listDatesUntil
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import java.util.Date

class Project(
    private var _id: Long? = null,
    private var _name: String = "",
    private var _allergenPersons: List<AllergenPerson> = listOf(),
    private var _mealPlan: MealPlan,
    private var _projectImage: Uri = Uri.EMPTY,
    private var _isOnline: Boolean = false,
    private var _dataVersion: Long = 0,
    private var _imageVersion: Long = 0
) {
    constructor(project: Project) : this(
        project.id,
        project.name,
        project.allergenPersons,
        project.mealPlan,
        project.projectImage,
        project.isOnline
    )
    val mealPlan: MealPlan
        get() = _mealPlan
    val id: Long
        get() = _id ?: 0

    val name: String
        get() = _name

    val startDate: Date
        get() = _mealPlan.startDate

    val endDate: Date
        get() = _mealPlan.endDate

    val meals: List<String>
        get() = _mealPlan.meals

    val allergenPersons: List<AllergenPerson>
        get() = _allergenPersons

    val projectImage: Uri
        get() = _projectImage

    val mealSlots: List<MealSlot>
        get() = startDate.listDatesUntil(endDate).map { date ->
            meals.map { MealSlot(date, it) }
        }.flatten()

    val isOnline: Boolean
        get() = _isOnline

    val dataVersion: Long
        get() = _dataVersion

    val imageVersion: Long
        get() = _imageVersion

    @DomainLayerRestricted
    fun withMetaData(metaData: ProjectMetaData): Project {
        val newProject = Project(this)
        newProject._id = metaData.stub.id
        newProject._name = metaData.stub.name
        newProject._projectImage = metaData.stub.imageUri
        newProject._mealPlan.setStartDate(metaData.startDate)
        newProject._mealPlan.setEndDate(metaData.endDate)
        return newProject
    }

    @DomainLayerRestricted
    fun withAllergenPersons(allergenPersons: List<AllergenPerson>): Project {
        val newProject = Project(this)
        newProject._allergenPersons = allergenPersons
        return newProject
    }

    @DomainLayerRestricted
    fun withMeals(meals: List<String>): Project {
        val newProject = Project(this)
        newProject._mealPlan.setMeals(meals)
        return newProject
    }

    @DomainLayerRestricted
    fun withMealPlan(plan: Map<MealSlot, Pair<RecipeStub, List<RecipeStub>>>): Project {
        val newProject = Project(this)
        newProject._mealPlan.setPlan(plan)
        return newProject
    }

    @DomainLayerRestricted
    fun withNumberChanges(numberChanges: Map<MealSlot, Int>): Project {
        val newProject = Project(this)
        newProject._mealPlan.setNumberChanges(numberChanges)
        return newProject
    }
}