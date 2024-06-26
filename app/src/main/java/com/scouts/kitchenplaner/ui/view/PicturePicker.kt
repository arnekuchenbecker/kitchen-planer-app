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

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

/**
 * A button with dialog to select an image. Clicking on the image makes it possible to select a new image.
 *
 * @param modifier additional modifier
 * @param onPathSelected action, if a new image is selected
 * @param path path of the selected image
 */
@Composable
fun PicturePicker(modifier: Modifier = Modifier, onPathSelected: (Uri?) -> Unit, path: Uri?) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        onPathSelected(uri)
    }
    Box (
        modifier = modifier
            .clickable {
                launcher.launch(arrayOf("image/*"))
            }
    ) {
        if (path != null && path != Uri.EMPTY) {
            AsyncImage(
                model = path,
                contentDescription = "Project Image",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Filled.AddCircle,
                contentDescription = "Choose Image",
                modifier = Modifier.fillMaxWidth().aspectRatio(1.0f))
        }
    }
}