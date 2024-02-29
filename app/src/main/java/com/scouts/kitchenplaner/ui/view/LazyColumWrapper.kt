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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun <T : Any> LazyColumnWrapper(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: List<T>,
    DisplayContent: @Composable (T, Int) -> Unit,
    DisplayLast: @Composable (T, Int) -> Unit = DisplayContent,
    DisplayEmpty: @Composable () -> Unit
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment
    ) {
        if (content.isNotEmpty()) {
            items(
                count = content.size - 1
            ) {
                DisplayContent(content[it], it)
            }
            item {
                DisplayLast(content[content.lastIndex], content.lastIndex)
            }
        } else {
            item { DisplayEmpty() }
        }
    }
}
