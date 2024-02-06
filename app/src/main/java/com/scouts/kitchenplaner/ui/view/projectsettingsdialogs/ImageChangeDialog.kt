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

import android.net.Uri
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scouts.kitchenplaner.ui.view.DeleteButton
import com.scouts.kitchenplaner.ui.view.PicturePicker

@Composable
fun ImageChangeDialog(
    currentImage: Uri,
    onDismissRequest: () -> Unit,
    onImageChange: (Uri) -> Unit
) {
    var uri by remember { mutableStateOf(currentImage) }
    SettingDialog(
        onDismissRequest = onDismissRequest,
        onConfirm = { onImageChange(uri) },
        title = "Bild ändern"
    ) {
        PicturePicker(
            onPathSelected = { uri = it ?: Uri.EMPTY },
            path = uri,
            modifier = Modifier
                .height(100.dp)
                .aspectRatio(1.0f)
        )

        DeleteButton(onClick = { uri = Uri.EMPTY })
    }
}