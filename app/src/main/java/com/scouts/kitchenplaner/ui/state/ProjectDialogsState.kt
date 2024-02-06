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

package com.scouts.kitchenplaner.ui.state

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.scouts.kitchenplaner.model.entities.MealPlan
import com.scouts.kitchenplaner.model.entities.MealSlot
import java.util.Date

class ProjectDialogsState (
    val onNameChange: (String) -> Unit,
    val onPictureChange: (Uri) -> Unit,
    val onDateChange: (Date, Date) -> Unit,
    val onNumbersChange: (Map<MealSlot, Int>) -> Unit,
    val projectId: Long,
    val currentImage: Uri,
    val startDate: Date,
    val endDate: Date,
    val mealPlan: MealPlan,
    val mealSlots: List<MealSlot>,
    val projectPublished: Boolean
) {
    var displayDialog by mutableStateOf(ProjectDialogValues.NONE)
}