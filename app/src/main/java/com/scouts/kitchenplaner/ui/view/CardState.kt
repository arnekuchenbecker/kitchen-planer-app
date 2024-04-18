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

package com.scouts.kitchenplaner.ui.view

import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Persists the state of a Expandable Card
 *
 * @param title Title of the card, which is visible the whole time
 * @param titleInteractions
 * @param onDelete
 * @param toBeDeleted
 * @param contentModifier customized modifier (not required)
 * @param content content visible, when the card is expanded
 */
data class CardState(val title: String,
                     val titleInteractions: @Composable () -> Unit = {},
                     val onDelete: () -> Unit,
                     val toBeDeleted: Boolean,
                     val contentModifier: Modifier = Modifier.heightIn(max = 100.dp),
                     val content: @Composable () -> Unit
)