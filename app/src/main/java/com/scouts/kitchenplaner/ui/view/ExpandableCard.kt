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

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scouts.kitchenplaner.ui.theme.KitchenPlanerTheme

private const val EXPAND_ANIMATION_DURATION = 300

/**
 * Card with title and possibility to expand the card.
 * When the card is expanded the title background changes color and a new expanded part is visible, where new content can be displayed
 * It is possible to click on the title and the arrow/icon.
 * The card can also be in a "to be deleted" mode, where a delete button is displayed instead of the arrow/icon.
 *
 * @param expanded whether the card is expanded
 * @param onCardArrowClick action what happens when clicking on the arrow/icon
 * @param onTitleClick action what happens when clicking on the title.
 * @param cardState persistent information about the status of the card and how a card looks like.
 */
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableCard(
    expanded: Boolean,
    onCardArrowClick: () -> Unit,
    onTitleClick: () -> Unit,
    cardState: CardState
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }

    val transition = rememberTransition(transitionState, label = "")

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPAND_ANIMATION_DURATION)
    }, label = "") {
        if (expanded) 0f else 180f
    }

    Card (
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            )
    ) {
        val bgColor = if (expanded && !cardState.toBeDeleted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
        val contentColor = if (expanded && !cardState.toBeDeleted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
        Box (
            modifier = Modifier
                .background(color = bgColor)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CardTitle(
                    modifier = Modifier.weight(1.0f),
                    title = cardState.title,
                    titleIcon = cardState.titleInteractions,
                    color = contentColor,
                    onTitleClick = onTitleClick
                )

                if(!cardState.toBeDeleted) {
                    CardArrow (
                        degrees = arrowRotationDegree,
                        onClick = onCardArrowClick,
                        color = contentColor
                    )
                } else {
                    DeleteButton(modifier = Modifier.padding(horizontal = 10.dp)) {
                        cardState.onDelete()
                    }
                }
            }
        }

        ExpandableContent(
            visible = expanded && !cardState.toBeDeleted,
            content = cardState.content,
            contentModifier = cardState.contentModifier
        )
    }
}

/**
 * A  button with a rotatable arrow for expanding a card
 * @param modifier additional modifier (not required)
 * @param degrees how much the arrow should be turned
 * @param onClick action, what happens when clicking on the arrow
 * @param color Color of the arrow
 */
@Composable
fun CardArrow(
    modifier: Modifier = Modifier,
    degrees: Float,
    onClick: () -> Unit,
    color: Color
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        content = {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Expandable Arrow",
                modifier = Modifier.rotate(degrees),
                tint = color
            )
        }
    )
}

/**
 * The title of the card with icon and action
 * @param modifier additional modifier (not required)
 * @param title text of the title
 * @param titleIcon icon which can be displayed on the left side of the title
 * @param color text color
 * @param onTitleClick action, what happens when clicking on the title
 */
@Composable
fun CardTitle(
    modifier: Modifier = Modifier,
    title: String,
    titleIcon: @Composable () -> Unit,
    color: Color,
    onTitleClick: () -> Unit
) {
    Row (modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        titleIcon()
        Spacer(modifier = Modifier.weight(0.5f))
        Text(
            text = title,
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    onTitleClick()
                },
            textAlign = TextAlign.Center,
            color = color
        )
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

/**
 * The content of the expandable card if it is expanded
 * @param visible whether the content is visible at the moment
 * @param initialVisibility whether the content was visible at the beginning. if initialVisibility and visible are different there is an animation
 * @param content what is displayed
 * @param contentModifier additional modifier
 */
@Composable
fun ExpandableContent(
    visible: Boolean = true,
    initialVisibility: Boolean = false,
    content: @Composable () -> Unit,
    contentModifier: Modifier
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(EXPAND_ANIMATION_DURATION)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(EXPAND_ANIMATION_DURATION)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Top,
            animationSpec = tween(EXPAND_ANIMATION_DURATION)
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween(EXPAND_ANIMATION_DURATION)
        )
    }

    AnimatedVisibility(
        visibleState = remember {
            MutableTransitionState(initialVisibility)
        }.apply {
            this.targetState = visible
        },
        enter = enterTransition,
        exit = exitTransition
    ) {
        Column(modifier = contentModifier
            .padding(8.dp)
        ) {
            Spacer(modifier = Modifier.heightIn(max = 100.dp))
            content()
        }
    }
}


@Composable
@Preview(showBackground = true)
fun CardPreview() {
    KitchenPlanerTheme(dynamicColor = false) {
        Column {
            var expanded by remember { mutableIntStateOf(3) }
            for (i in IntRange(0, 4)) {
                ExpandableCard(
                    expanded = i == expanded,
                    onCardArrowClick = { expanded = i },
                    cardState = CardState(
                        title = "Card Number $i",
                        onDelete = {},
                        toBeDeleted = false
                    ) {
                        Text("SLADÖFJAKDSLJFLKASDJFAÖLS $i\nöalskdjfölkasdjf $i")
                    },
                    onTitleClick = {})
            }
        }
    }
}