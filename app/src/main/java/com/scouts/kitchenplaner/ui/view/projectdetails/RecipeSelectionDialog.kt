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

package com.scouts.kitchenplaner.ui.view.projectdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scouts.kitchenplaner.model.entities.RecipeStub
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper
import com.scouts.kitchenplaner.ui.view.OverviewField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSelectionDialog(
    onDismissRequest: () -> Unit,
    onNavigateToRecipeCreation: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSelection: (RecipeStub) -> Unit,
    recipeQuery: String,
    searchResults: List<RecipeStub>
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            modifier = Modifier.fillMaxHeight(0.5f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val keyboardController = LocalSoftwareKeyboardController.current

                DockedSearchBar(
                    query = recipeQuery,
                    onQueryChange = onQueryChange,
                    active = true,
                    onActiveChange = {},
                    onSearch = { keyboardController?.hide() },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .weight(1f)
                        .padding(10.dp),
                    placeholder = { Text(text = "Rezept suchen...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Recipe"
                        )
                    },
                    trailingIcon = {
                        if (recipeQuery.isNotBlank()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear search field"
                                )
                            }
                        }
                    }
                ) {
                    LazyColumnWrapper(
                        content = searchResults,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        DisplayContent = { it, _ ->
                            OverviewField (
                                modifier = Modifier.height(50.dp).padding(horizontal = 5.dp),
                                onClick = { onSelection(it) },
                                imageUri = it.imageURI,
                                imageDescription = "Recipe Image for ${it.name}",
                                text = it.name
                            )
                        },
                        DisplayEmpty = {
                            Box (modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                            ) {
                                Text(
                                    text = "Es wurden keine Rezepte mit diesem Namen gefunden.",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.Center),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    )
                }
                Button(onClick = onNavigateToRecipeCreation) {
                    Text("Neues Rezept erstellen")
                }
            }
        }
    }
}
