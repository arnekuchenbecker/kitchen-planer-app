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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val EXPAND_ANIMATION_DURATION = 300

/**
 * Customized side drawer, usable for edition options.
 *
 * @param modifier additional modifier
 * @param expand whether the drawer is expanded
 * @param content The content which is displayed while the drawer is expanded
 */
@Composable
fun SideDrawer(
    modifier: Modifier = Modifier,
    expand: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    val enterTransition = remember {
        expandHorizontally(
            expandFrom = Alignment.End,
            animationSpec = tween(EXPAND_ANIMATION_DURATION)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(EXPAND_ANIMATION_DURATION)
        )
    }
    val exitTransition = remember {
        shrinkHorizontally (
            // Expand from the top.
            shrinkTowards = Alignment.End,
            animationSpec = tween(EXPAND_ANIMATION_DURATION)
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween(EXPAND_ANIMATION_DURATION)
        )
    }

    Box(modifier = modifier
        .fillMaxSize()
    ){
        AnimatedVisibility(
            visibleState = remember {
                MutableTransitionState(false)
            }.apply {
                this.targetState = expand
            },
            enter = enterTransition,
            exit = exitTransition,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Surface (
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.7f),
                shape = RoundedCornerShape(15.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shadowElevation = 15.dp
            ) {
                Column {
                    this.content()
                }
            }
        }
    }
}