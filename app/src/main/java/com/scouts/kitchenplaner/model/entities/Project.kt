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

package com.scouts.kitchenplaner.model.entities

import android.net.Uri
import com.scouts.kitchenplaner.listDatesUntil
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import java.util.Date

class Project(
    private var _id: Long? = null,
    private var _name: String = "",
    initialStartDate: Date,
    initialEndDate: Date,
    private var _allergenPersons: List<AllergenPerson> = listOf(),
    initialMeals: List<String> = listOf(),
    private var _projectImage: Uri = Uri.EMPTY
){
    private var _mealPlan = MealPlan(initialStartDate, initialEndDate, initialMeals)
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

    @DomainLayerRestricted
    fun setID(id: Long) {
        _id = id
    }

    @DomainLayerRestricted
    fun setName(name: String) {
        _name = name
    }

    @DomainLayerRestricted
    fun setStartDate(startDate: Date) {
        _mealPlan.setStartDate(startDate)
    }

    @DomainLayerRestricted
    fun setEndDate(endDate: Date) {
        _mealPlan.setEndDate(endDate)
    }

    @DomainLayerRestricted
    fun setImageUri(uri: Uri) {
        _projectImage = uri
    }

    @DomainLayerRestricted
    fun setAllergenPersons(allergenPersons: List<AllergenPerson>) {
        _allergenPersons = allergenPersons
    }

    override fun equals(other: Any?): Boolean = (other is Project)
            && _id == other._id
            && _name == other._name
            && _allergenPersons == other._allergenPersons
            && _projectImage == other._projectImage
            && _mealPlan == other._mealPlan
}