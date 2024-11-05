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

import com.scouts.kitchenplaner.datalayer.entities.UnitConversionEntity
import com.scouts.kitchenplaner.model.DomainLayerRestricted

/**
 * Entity representing a possible Unit Conversion. To apply a Unit Conversion to an ingredient, the
 * ingredient's name has to match [ingredientMatcher] and the ingredient's unit has to be the same
 * as [_sourceUnit]
 *
 * @param ingredientMatcher Regex identifying what ingredients this UnitConversion can be applied to
 * @param _sourceUnit The unit this UnitConversion converts from
 * @param _destinationUnit The unit this UnitConversion converts to
 * @param factor The factor to apply to an ingredient to convert from [_sourceUnit] to
 * [_destinationUnit]
 */
class UnitConversion(
    private val ingredientMatcher: Regex,
    private val _sourceUnit: String,
    private val _destinationUnit: String,
    private val factor: Double
) {
    /**
     * Applies this UnitConversion to the given ingredient, returning an Ingredient with the unit
     * [_destinationUnit] by multiplying the ingredient's amount by [factor] if this UnitConversion
     * is applicable to the Ingredient (see [isApplicable]) or null otherwise
     *
     * @param ingredient The ingredient to which to apply this UnitConversion
     *
     * @return An Ingredient to which this UnitConversion was applied if it is applicable or null
     * otherwise
     */
    fun apply(ingredient: Ingredient): Ingredient? {
        return if (isApplicable(ingredient)) {
            Ingredient(ingredient.name, ingredient.amount * factor, _destinationUnit)
        } else {
            null
        }
    }

    /**
     * Checks whether thisUnitConversion is applicable to the given Ingredient, i.e. if the
     * [ingredientMatcher] matches the ingredient's name and the ingredient's unit is equal to
     * [_sourceUnit]
     *
     * @param ingredient The ingredient to check whether this UnitConversion is applicable to
     *
     * @return Whether this UnitConversion is applicable to the given Ingredient
     */
    fun isApplicable(ingredient: Ingredient): Boolean {
        return ingredientMatcher.matches(ingredient.name) && ingredient.unit == _sourceUnit
    }

    /**
     * Converts this UnitConversion to a DataLayer Entity
     *
     * @param projectID The project this UnitConversion belongs to
     *
     * @return A UnitConversionEntity representing this UnitConversion
     */
    fun toDataLayerEntity(projectID: Long): UnitConversionEntity {
        return UnitConversionEntity(
            projectID,
            ingredientMatcher.pattern,
            _sourceUnit,
            _destinationUnit,
            factor
        )
    }

    @DomainLayerRestricted
    val pattern: String
        get() = ingredientMatcher.pattern

    @DomainLayerRestricted
    val sourceUnit: String
        get() = _sourceUnit

    @DomainLayerRestricted
    val destinationUnit: String
        get() = _destinationUnit

    @OptIn(DomainLayerRestricted::class)
    override fun toString(): String {
        return "($pattern: [$sourceUnit -> $destinationUnit])"
    }

    override fun hashCode(): Int {
        return ingredientMatcher.pattern.hashCode() + _sourceUnit.hashCode() + _destinationUnit.hashCode() + factor.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is UnitConversion) {
            ingredientMatcher.pattern == other.ingredientMatcher.pattern
                    && _sourceUnit == other._sourceUnit
                    && _destinationUnit == other._destinationUnit
                    && factor == other.factor
        } else {
            false
        }
    }
}