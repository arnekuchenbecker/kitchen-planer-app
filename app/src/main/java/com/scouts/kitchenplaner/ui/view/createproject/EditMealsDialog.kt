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

package com.scouts.kitchenplaner.ui.view.createproject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scouts.kitchenplaner.ui.view.DeleteButton
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper


@Composable
fun EditMealsDialog(onDismissRequest: () -> Unit, onAdd: (String) -> Unit, onRemove: (Int) -> Unit, meals: List<String>) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.fillMaxHeight(0.5f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column (modifier = Modifier.padding(20.dp)) {
                var removingIndex by remember { mutableIntStateOf(-1) }
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    var newMeal by remember { mutableStateOf("") }
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = newMeal,
                        onValueChange = {
                            newMeal = it },
                        trailingIcon = {
                            Button(
                                onClick = {
                                    if (newMeal != "") {
                                        onAdd(newMeal)
                                        newMeal = ""
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Filled.Add, "Add Meal")
                            } },
                        singleLine = true
                    )
                }

                LazyColumnWrapper(
                    content = meals,
                    DisplayContent = { meal, index ->
                        EditMealDialogListItem(
                            mealName = meal,
                            toBeDeleted = index == removingIndex,
                            displayDivider = true,
                            onDelete = {
                                removingIndex = if (index == removingIndex) {
                                    onRemove(index)
                                    -1
                                } else {
                                    index
                                } },
                            onCancelDelete = {
                                removingIndex = -1
                            }
                        ) },
                    DisplayLast = { meal, index ->
                        EditMealDialogListItem(
                            mealName = meal,
                            toBeDeleted = index == removingIndex,
                            displayDivider = false,
                            onDelete = {
                                removingIndex = if (index == removingIndex) {
                                    onRemove(index)
                                    -1
                                } else {
                                    index
                                } },
                            onCancelDelete = {
                                removingIndex = -1
                            }
                        ) },
                    DisplayEmpty = {
                        Box(
                            modifier = Modifier
                                .padding(0.dp, 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text("Keine Mahlzeiten")
                        }
                    })
            }
        }
    }
}

@Composable
fun EditMealDialogListItem(mealName: String, toBeDeleted: Boolean, displayDivider: Boolean, onDelete: () -> Unit, onCancelDelete: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        val boxModifier = if (toBeDeleted) {
            Modifier
                .padding(0.dp, 10.dp)
                .fillMaxHeight()
                .weight(1.0f)
        } else {
            Modifier
                .padding(0.dp, 10.dp)
                .fillMaxSize()
        }
        Box(modifier = boxModifier.clickable {
            if (!toBeDeleted) {
                onDelete()
            } else {
                onCancelDelete()
            }
        }) {
            Text(mealName)
        }
        if (toBeDeleted) {
            DeleteButton (modifier = Modifier
                .fillMaxHeight(0.7f)
                .padding(horizontal = 5.dp)) {
                onDelete()
            }
        }
    }

    if (displayDivider) {
        HorizontalDivider(thickness = 1.dp)
    }

}