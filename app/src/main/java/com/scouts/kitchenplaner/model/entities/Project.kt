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
import com.scouts.kitchenplaner.model.DomainLayerRestricted
import java.util.Date

/**
 * NOT FINAL --> Adjust when stuff actually happens to projects
 */
interface Project {
    val id: Long?
    val name: String
    val startDate: Date
    val endDate: Date
    val meals: List<String>
    val allergenPersons: List<AllergenPerson>
    val projectImage: Uri
}

@DomainLayerRestricted
interface MutableProject : Project {
    override var id: Long?
    override var name: String
    override var startDate: Date
    override var endDate: Date
    override var projectImage: Uri

    fun addMeal(meal: String, index: Int)
}

@OptIn(DomainLayerRestricted::class)
private class ProjectImpl(
    private val _meals: MutableList<String> = mutableListOf(),
    private val _allergenPersons: MutableList<AllergenPerson> = mutableListOf(),
    override var projectImage: Uri = Uri.EMPTY,
    override var id: Long? = null,
    override var name: String = "",
    override var startDate: Date = Date(0),
    override var endDate: Date = Date(0)
) : MutableProject {
    override val meals: List<String>
        get() = _meals

    override val allergenPersons: List<AllergenPerson>
        get() = _allergenPersons
    override fun addMeal(meal: String, index: Int) {
        if (index >= 0 && index < _meals.size) {
            _meals.add(index, meal)
        } else {
            _meals.add(meal)
        }
    }
}

class ProjectBuilder {
    private val _meals: MutableList<String> = mutableListOf()
    private val _allergenPersons: MutableList<AllergenPerson> = mutableListOf()
    private var _projectImage: Uri = Uri.EMPTY
    private var _id: Long? = null
    private var _name: String = ""
    private var _startDate: Date = Date(0)
    private var _endDate: Date = Date(0)

    fun id(id: Long?) : ProjectBuilder {
        _id = id
        return this
    }

    fun name(name: String) : ProjectBuilder {
        _name = name
        return this
    }

    fun projectImage(uri: Uri) : ProjectBuilder {
        _projectImage = uri
        return this
    }

    fun meals(meals: List<String>) : ProjectBuilder {
        _meals.addAll(meals)
        return this
    }

    fun allergenPersons(allergenPersons: List<AllergenPerson>) : ProjectBuilder {
        _allergenPersons.addAll(allergenPersons)
        return this
    }

    fun startDate(date: Date) : ProjectBuilder {
        _startDate = Date(date.time)
        return this
    }

    fun endDate(date: Date) : ProjectBuilder {
        _endDate = Date(date.time)
        return this
    }

    fun build() : Project {
        return createProject()
    }

    @DomainLayerRestricted
    fun buildMutable() : MutableProject {
        return createProject()
    }

    private fun createProject() : ProjectImpl {
        return ProjectImpl(
            _meals,
            _allergenPersons,
            _projectImage,
            _id,
            _name,
            _startDate,
            _endDate
        )
    }
}

@DomainLayerRestricted
fun Project.toMutableProject() : MutableProject {
    return ProjectBuilder()
        .id(id)
        .name(name)
        .startDate(startDate)
        .endDate(endDate)
        .projectImage(projectImage)
        .meals(meals)
        .allergenPersons(allergenPersons)
        .buildMutable()
}
