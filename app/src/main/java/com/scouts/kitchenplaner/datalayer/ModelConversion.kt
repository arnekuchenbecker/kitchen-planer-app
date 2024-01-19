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

package com.scouts.kitchenplaner.datalayer

import com.scouts.kitchenplaner.datalayer.entities.AllergenEntity
import com.scouts.kitchenplaner.datalayer.entities.AllergenPersonEntity
import com.scouts.kitchenplaner.datalayer.entities.DietarySpeciality
import com.scouts.kitchenplaner.datalayer.entities.DietaryTypes
import com.scouts.kitchenplaner.datalayer.entities.IngredientEntity
import com.scouts.kitchenplaner.datalayer.entities.IngredientGroupEntity
import com.scouts.kitchenplaner.datalayer.entities.MealEntity
import com.scouts.kitchenplaner.datalayer.entities.PersonNumberChangeEntity
import com.scouts.kitchenplaner.datalayer.entities.ProjectEntity
import com.scouts.kitchenplaner.datalayer.entities.RecipeEntity
import com.scouts.kitchenplaner.datalayer.entities.ShoppingListEntity
import com.scouts.kitchenplaner.datalayer.entities.ShoppingListEntryEntity
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.entities.ShoppingList

fun Project.toDataLayerEntity(): ProjectEntity {
    return ProjectEntity(
        id = (id ?: 0),
        name = name,
        startDate = startDate,
        endDate = endDate,
        imageUri = projectImage.path ?: ""
    )
}

fun AllergenPerson.toDataLayerEntity(projectId: Long?): Pair<AllergenPersonEntity, List<AllergenEntity>> {
    return Pair(AllergenPersonEntity(
        name = name,
        projectId = projectId ?: 0,
        arrivalDate = arrivalDate,
        arrivalMeal = arrivalMeal,
        departureDate = departureDate,
        departureMeal = departureMeal
    ), allergens.map {
        AllergenEntity(projectId ?: 0, name, it.allergen, it.traces)
    })
}

fun ProjectEntity.toModelEntity(
    meals: List<MealEntity>,
    allergenPersons: List<AllergenPersonEntity>,
    allergens: List<AllergenEntity>,
    personNumbers: List<PersonNumberChangeEntity> //TODO when available in DomainLayer
) : Project {
    return Project(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        meals = meals.map { it.name },
        allergenPersons = allergenPersons.map { person ->
            AllergenPerson(
                name = person.name,
                arrivalDate = person.arrivalDate,
                arrivalMeal = person.arrivalMeal,
                departureDate = person.departureDate,
                departureMeal = person.departureMeal,
                allergens = allergens
                    .filter { it.name == person.name }
                    .map { Allergen(it.allergen, it.traces) }
            )
        }
    )
}

fun Allergen.toDataLayerEntity(projectId: Long, name: String) : AllergenEntity {
    return AllergenEntity(projectId, name, allergen, traces)
}

fun Recipe.toDataLayerEntity(): Pair<RecipeEntity, List<DietarySpeciality>> {

    val speciality: MutableList<DietarySpeciality> = mutableListOf()
    speciality.addAll(allergen.map {
        DietarySpeciality(id, DietaryTypes.ALLERGEN, it)
    })
    speciality.addAll(traces.map { DietarySpeciality(id, DietaryTypes.TRACE, it) })
    speciality.addAll(freeOfAllergen.map { DietarySpeciality(id, DietaryTypes.FREE_OF, it) })
    return Pair(
        RecipeEntity(
            id = id,
            title = name,
            imageURI = imageURI.toString(),
            description = description,
            numberOfPeople = numberOfPeople
        ), speciality
    )
}

fun IngredientGroup.toDataLayerEntity(recipeID: Long): Pair<IngredientGroupEntity, List<IngredientEntity>> {
    return Pair(
        IngredientGroupEntity(name = name, recipeID),
        ingredients.map {
            IngredientEntity(
                recipe = recipeID,
                ingredientGroup = name,
                name = it.name,
                unit = it.unit,
                amount = it.amount
            )
        }
    )
}

fun ShoppingList.toDataLayerEntity(projectId: Long): Pair<ShoppingListEntity, List<ShoppingListEntryEntity>> {
    return Pair(
        ShoppingListEntity(
            id = id ?: 0,
            name = name,
            projectId = projectId
        ),
        items.map {
            ShoppingListEntryEntity(id ?: 0, projectId, it.name, it.amount, it.unit)
        }
    )
}