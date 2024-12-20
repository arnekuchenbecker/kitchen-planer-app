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

package com.scouts.kitchenplaner.ui.view.createrecipe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Dialog for importing a recipe from Chefkoch.de
 *
 * @param onDismissRequest Callback function to close the dialog when requested by the user
 * @param importRecipe Callback function to import the recipe specified by the given source (either
 *                     a chefkoch URL or the ID of a chefkoch recipe)
 */
@Composable
fun RecipeImportDialog(
    onDismissRequest: () -> Unit,
    importRecipe: (String) -> Boolean
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = RoundedCornerShape(15.dp)) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var textFieldIsError by remember { mutableStateOf(false) }
                var source by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = { Text("Chefkoch-URL oder ID") },
                    modifier = Modifier.padding(bottom = 10.dp),
                    singleLine = true,
                    isError = textFieldIsError,
                    supportingText = if (textFieldIsError) {
                        { Text("Invalid Import Source") }
                    } else {
                        null
                    }
                )
                Button(
                    onClick = {
                        val success = importRecipe(source)
                        if (success) {
                            onDismissRequest()
                        } else {
                            textFieldIsError = true
                        }
                    }
                ) {
                    Text("Rezept Importieren")
                }
            }
        }
    }
}