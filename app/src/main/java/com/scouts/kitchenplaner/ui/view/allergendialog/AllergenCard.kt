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

package com.scouts.kitchenplaner.ui.view.allergendialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scouts.kitchenplaner.toDateString
import com.scouts.kitchenplaner.ui.view.CardState
import com.scouts.kitchenplaner.ui.view.DeleteButton
import com.scouts.kitchenplaner.ui.view.ExpandableCard

/**
 * Card in a list which represents an allergen person.
 * It contains the name, amount of allergens and interval of presence of the allergen person.
 * The title can be clicked and the allergen person can be deleted.
 * When expanded all allergens are presented. When clicking on an allergen it can be deleted.
 *
 * @param name Name of the allergen person
 * @param allergens All allergens of the allergen person including if traces are important
 * @param arrivalDate The date when the allergen person arrives
 * @param arrivalMeal The first meal of the allergen person
 * @param departureDate The date when the allergen person leaves
 * @param departureMeal The last meal of the allergen person
 * @param onTitleClick Callback function when clicking on the title
 * @param onDelete Callback function when deleting the allergen person
 * @param onItemDelete Callback function when deleting an allergen
 * @param toBeDeleted Whether the delete button for deleting the allergen person is present
 * @param toggleExpand Callback function to toggle whether the card is expanded
 * @param expand Whether the card is expanded
 */
@Composable
fun AllergenCard(
    name: String,
    allergens: List<Pair<String, Boolean>>,
    arrivalDate: Long?,
    arrivalMeal: String,
    departureDate: Long?,
    departureMeal: String,
    onTitleClick: () -> Unit,
    onDelete: () -> Unit,
    onItemDelete: (Pair<String, Boolean>) -> Unit,
    toBeDeleted: Boolean,
    toggleExpand: () -> Unit,
    expand: Boolean
) {
    val dateString = if (arrivalDate != null && departureDate != null) {
        "\n${arrivalDate.toDateString()} ($arrivalMeal) - ${departureDate.toDateString()} ($departureMeal)"
    } else {
        ""
    }
    ExpandableCard(
        expanded = expand,
        onCardArrowClick = toggleExpand,
        onTitleClick = onTitleClick,
        cardState = CardState(title = "$name\n(${allergens.size} EBs)$dateString", onDelete = onDelete, toBeDeleted = toBeDeleted) {
            LazyColumn {
                items(allergens) { (allergen, traces) ->
                    var itemToBeDeleted by remember { mutableStateOf(false) }
                    val traceString = if (traces) " (Spuren)" else ""
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable { itemToBeDeleted = !itemToBeDeleted }
                                .weight(1.0f),
                            text = "$allergen$traceString")

                        if (itemToBeDeleted) {
                            DeleteButton {
                                onItemDelete(Pair(allergen, traces))
                                itemToBeDeleted = false
                            }
                        }

                    }
                }
            }
        }
    )
}