/*
 * KitchenPlanerApp is the android app frontend for the KitchenPlaner, a tool
 * to cooperatively plan a meal plan for a campout.
 * Copyright (C) 2023  Arne Kuchenbecker, Antonia Heiming
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

package com.scouts.kitchenplaner.ui.view.createproject

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
import com.scouts.kitchenplaner.ui.view.CardState
import com.scouts.kitchenplaner.ui.view.DeleteButton
import com.scouts.kitchenplaner.ui.view.ExpandableCard

@Composable
fun AllergenCard(name: String, allergens: List<Pair<String, Boolean>>, onTitleClick: () -> Unit, onDelete: () -> Unit, onItemDelete: (Pair<String, Boolean>) -> Unit, toBeDeleted: Boolean) {
    var expand by remember { mutableStateOf(false) }
    ExpandableCard(
        expanded = expand,
        onCardArrowClick = { expand = !expand },
        onTitleClick = onTitleClick,
        cardState = CardState("$name (${allergens.size} EBs)", onDelete, toBeDeleted) {
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