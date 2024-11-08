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
import java.math.BigDecimal

/**
 * Entity representing a possible Unit Conversion. To apply a Unit Conversion to an ingredient, the
 * ingredient's name can be checked against some condition and the ingredient's unit has to be the same
 * as [_sourceUnit]
 *
 * @param _sourceUnit The unit this UnitConversion converts from
 * @param _destinationUnit The unit this UnitConversion converts to
 * @param factor The factor to apply to an ingredient to convert from [_sourceUnit] to
 * [_destinationUnit]
 */
sealed class UnitConversion(
    protected val _sourceUnit: String,
    protected val _destinationUnit: String,
    protected val factor: BigDecimal
) {
    companion object {
        /**
         * Creates a new UnitConversion matching any ingredient whose name matches the given Regex
         *
         * @param ingredientMatcher The regex for matching ingredient names
         * @param sourceUnit The unit the new UnitConversion converts from
         * @param destinationUnit The unit the new UnitConversion converts to
         * @param factor The factor to apply to an ingredient to convert from [sourceUnit] to [destinationUnit]
         */
        fun of(
            ingredientMatcher: Regex,
            sourceUnit: String,
            destinationUnit: String,
            factor: BigDecimal
        ): UnitConversion {
            return RegexConversion(ingredientMatcher, sourceUnit, destinationUnit, factor)
        }

        /**
         * Creates a new UnitConversion matching any ingredient whose name is equal to the given
         * String
         *
         * @param text The String for matching ingredient names
         * @param sourceUnit The unit the new UnitConversion converts from
         * @param destinationUnit The unit the new UnitConversion converts to
         * @param factor The factor to apply to an ingredient to convert from [sourceUnit] to [destinationUnit]
         */
        fun of(
            text: String,
            sourceUnit: String,
            destinationUnit: String,
            factor: BigDecimal
        ): UnitConversion {
            return TextConversion(text, sourceUnit, destinationUnit, factor)
        }
    }

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
            Ingredient(ingredient.name, ingredient.amount * factor.toDouble(), _destinationUnit)
        } else {
            null
        }
    }

    /**
     * Checks whether this UnitConversion is applicable to the given Ingredient, i.e. whether the
     * ingredient name matches whatever restrictions are imposed on this UnitConversion and whether
     * the ingredient's unit is the same as [_sourceUnit]
     *
     * @param ingredient The ingredient to check whether this UnitConversion is applicable to
     *
     * @return Whether this UnitConversion is applicable to the given Ingredient
     */
    fun isApplicable(ingredient: Ingredient) : Boolean {
        return ingredient.unit == _sourceUnit && matches(ingredient)
    }

    protected abstract fun matches(ingredient: Ingredient) : Boolean

    /**
     * Converts this UnitConversion to a DataLayer Entity
     *
     * @param projectID The project this UnitConversion belongs to
     *
     * @return A UnitConversionEntity representing this UnitConversion
     */
    abstract fun toDataLayerEntity(projectID: Long): UnitConversionEntity

    /**
     * String representation of the means for identifying which ingredients this UnitConversion can
     * be applied to
     */
    @DomainLayerRestricted
    abstract val representation: String

    /**
     * The unit this UnitConversion converts from
     */
    @DomainLayerRestricted
    val sourceUnit: String
        get() = _sourceUnit

    /**
     * The unit this UnitConversion converts to
     */
    @DomainLayerRestricted
    val destinationUnit: String
        get() = _destinationUnit

    @OptIn(DomainLayerRestricted::class)
    override fun toString(): String {
        return "($representation: [$sourceUnit -> $destinationUnit])"
    }

    @OptIn(DomainLayerRestricted::class)
    override fun equals(other: Any?): Boolean {
        return if (other is UnitConversion) {
            factor == other.factor
                    && _sourceUnit == other._sourceUnit
                    && _destinationUnit == other._destinationUnit
                    && representation == other.representation
        } else {
            false
        }
    }

    @OptIn(DomainLayerRestricted::class)
    override fun hashCode(): Int {
        var result = _sourceUnit.hashCode()
        result = 31 * result + _destinationUnit.hashCode()
        result = 31 * result + factor.hashCode()
        result = 31 * result + representation.hashCode()
        return result
    }

    class RegexConversion internal constructor(
        private val regex: Regex,
        sourceUnit: String,
        destinationUnit: String,
        factor: BigDecimal
    ) : UnitConversion(sourceUnit, destinationUnit, factor) {
        @DomainLayerRestricted
        override val representation: String
            get() = regex.pattern

        override fun toDataLayerEntity(projectID: Long): UnitConversionEntity {
            return UnitConversionEntity(
                projectID,
                regex.pattern,
                true,
                _sourceUnit,
                _destinationUnit,
                factor.toString()
            )
        }

        override fun matches(ingredient: Ingredient): Boolean {
            return regex.matches(ingredient.name)
        }
    }

     class TextConversion internal constructor(
        private val _text: String,
        sourceUnit: String,
        destinationUnit: String,
        factor: BigDecimal
    ) : UnitConversion(sourceUnit, destinationUnit, factor) {
        @DomainLayerRestricted
        override val representation: String
            get() = _text

        override fun matches(ingredient: Ingredient): Boolean {
            return ingredient.name == _text
        }

        override fun toDataLayerEntity(projectID: Long): UnitConversionEntity {
            return UnitConversionEntity(
                projectID,
                _text,
                false,
                _sourceUnit,
                _destinationUnit,
                factor.toString()
            )
        }
    }
}