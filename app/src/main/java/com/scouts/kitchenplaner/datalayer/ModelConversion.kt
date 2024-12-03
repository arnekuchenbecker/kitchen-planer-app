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

package com.scouts.kitchenplaner.datalayer

import android.net.Uri
import com.scouts.kitchenplaner.datalayer.entities.AllergenEntity
import com.scouts.kitchenplaner.datalayer.entities.AllergenPersonEntity
import com.scouts.kitchenplaner.datalayer.entities.DietarySpecialityEntity
import com.scouts.kitchenplaner.datalayer.entities.DynamicShoppingListEntryEntity
import com.scouts.kitchenplaner.datalayer.entities.IngredientEntity
import com.scouts.kitchenplaner.datalayer.entities.ProjectEntity
import com.scouts.kitchenplaner.datalayer.entities.RecipeEntity
import com.scouts.kitchenplaner.datalayer.entities.ShoppingListEntity
import com.scouts.kitchenplaner.datalayer.entities.StaticShoppingListEntryEntity
import com.scouts.kitchenplaner.model.entities.Allergen
import com.scouts.kitchenplaner.model.entities.AllergenPerson
import com.scouts.kitchenplaner.model.entities.DietarySpeciality
import com.scouts.kitchenplaner.model.entities.DietaryTypes
import com.scouts.kitchenplaner.model.entities.IngredientGroup
import com.scouts.kitchenplaner.model.entities.Project
import com.scouts.kitchenplaner.model.entities.ProjectMetaData
import com.scouts.kitchenplaner.model.entities.ProjectStub
import com.scouts.kitchenplaner.model.entities.Recipe
import com.scouts.kitchenplaner.model.entities.shoppinglists.ShoppingList

fun Project.toDataLayerEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        imageUri = projectImage.toString(),
        isArchived = false,
        onlineID = null,
        dataVersion = dataVersion,
        imageVersion = imageVersion
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

fun ProjectEntity.toModelEntity(): ProjectMetaData {
    return ProjectMetaData(ProjectStub(name, id, Uri.parse(imageUri)), startDate, endDate, onlineID)
}

fun Allergen.toDataLayerEntity(projectId: Long, name: String): AllergenEntity {
    return AllergenEntity(projectId, name, allergen, traces)
}

fun AllergenPersonEntity.toModelEntity(allergens: List<AllergenEntity>): AllergenPerson {
    return AllergenPerson(
        name, allergens.map {
            Allergen(it.allergen, it.traces)
        }, arrivalDate, arrivalMeal, departureDate, departureMeal
    )
}

fun Recipe.toDataLayerEntity(): Pair<RecipeEntity, List<DietarySpecialityEntity>> {

    val speciality: MutableList<DietarySpecialityEntity> = mutableListOf()
    speciality.addAll(allergens.map {
        DietarySpecialityEntity(id, DietaryTypes.ALLERGEN, it)
    })
    speciality.addAll(traces.map { DietarySpecialityEntity(id, DietaryTypes.TRACE, it) })
    speciality.addAll(freeOfAllergen.map { DietarySpecialityEntity(id, DietaryTypes.FREE_OF, it) })
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

fun IngredientGroup.toDataLayerEntity(recipeID: Long): List<IngredientEntity> {
    return ingredients.map {
        IngredientEntity(
            recipe = recipeID,
            ingredientGroup = name,
            name = it.name,
            unit = it.unit,
            amount = it.amount
        )
    }
}

/**
 * Converts this shopping list to data layer entities, inserting the given project ID
 *
 * @param projectId The ID of the project this shopping list belongs to
 *
 * @return A Triple consisting of the ShoppingListEntity representing the metadata of this shopping
 *         list, a list containing entities for all dynamic entries and a list containing all static
 *         entries of this shopping list
 */
fun ShoppingList.toDataLayerEntity(projectId: Long): Triple<ShoppingListEntity, List<DynamicShoppingListEntryEntity>, List<StaticShoppingListEntryEntity>> {
    return Triple(
        ShoppingListEntity(id = id, name = name, projectId = projectId),
        items.mapNotNull { it.toDynamicEntity(id, projectId) },
        items.mapNotNull { it.toStaticEntity(id, projectId) }
    )
}

fun DietarySpecialityEntity.toModelEntity(): DietarySpeciality {
    return DietarySpeciality(speciality, type)
}