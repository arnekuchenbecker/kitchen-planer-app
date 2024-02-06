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

package com.scouts.kitchenplaner.ui.view.projectsettingsdialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.scouts.kitchenplaner.model.entities.MealSlot
import com.scouts.kitchenplaner.toDateString
import com.scouts.kitchenplaner.ui.view.LazyColumnWrapper

@Composable
fun NumberChangeDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Map<MealSlot, Int>) -> Unit,
    presentPersons: Map<MealSlot, Int>,
    mealSlots: List<MealSlot>
) {
    val numberChanges: SnapshotStateMap<MealSlot, Int> = remember {
        SnapshotStateMap<MealSlot, Int>().apply {
            mealSlots.forEach {
                val index = mealSlots.indexOf(it)
                if (index == 0) {
                    put(it, presentPersons[it] ?: 0)
                } else {
                    val prev = presentPersons[mealSlots[index - 1]] ?: 0
                    put(it, (presentPersons[it] ?: 0) - prev)
                }
            }
        }
    }
    var changingMealSlot: MealSlot? by remember { mutableStateOf(null) }
    SettingDialog(
        onDismissRequest = onDismissRequest,
        title = "Ankunft / Abfahrt ändern",
        onConfirm = { onConfirm(numberChanges) }) {
        LazyColumnWrapper(
            content = mealSlots,
            modifier = Modifier.heightIn(max = 400.dp),
            DisplayContent = { slot, _ ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val change = numberChanges[slot] ?: 0
                    val color = if (change < 0) {
                        Color.Red
                    } else if (change == 0) {
                        Color.Yellow
                    } else {
                        Color.Green
                    }
                    Text(
                        text = "${if (change > 0) "+" else ""}$change",
                        color = color,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickable {
                                changingMealSlot = slot
                            }
                            .width(50.dp)
                            .border(
                                3.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(10.dp)
                            )
                            .height(30.dp)
                    )
                    Spacer(modifier = Modifier.padding(3.dp))
                    Text(text = "${slot.date.time.toDateString()}, ${slot.meal}")
                }
            },
            DisplayEmpty = {
                Text(text = "Dieses Projekt hat keine Mahlzeiten")
            }
        )
    }

    if (changingMealSlot != null) {
        var newChange by remember { mutableStateOf("") }
        SettingDialog(
            onDismissRequest = { changingMealSlot = null },
            title = "${changingMealSlot?.date?.time?.toDateString()}, ${changingMealSlot?.meal}",
            onConfirm = {
                val slot = changingMealSlot
                if (slot != null) {
                    numberChanges[slot] = newChange.toIntOrNull() ?: 0
                    println("Updated: ${numberChanges[slot]}")
                }
            }
        ) {
            val pattern = remember { Regex("^[+-]?\\d*\$") }

            OutlinedTextField(
                value = newChange,
                onValueChange = {
                    if (it.isEmpty() || it.matches(pattern)) {
                        newChange = it
                    } else {
                        println("Didn't match regex :(")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}