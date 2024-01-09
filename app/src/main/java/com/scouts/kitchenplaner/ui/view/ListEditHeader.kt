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

package com.scouts.kitchenplaner.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListEditHeader(
    onClick: () -> Unit,
    title: String
) {
    Row (modifier = Modifier
        .fillMaxWidth()
        .height(70.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.4f)
            .padding(0.dp, 0.dp, 5.dp, 0.dp)) {
            Text(title)
        }
        Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxHeight()) {
            Button(onClick = onClick) {
                Text("Bearbeiten")
            }
        }
    }
}