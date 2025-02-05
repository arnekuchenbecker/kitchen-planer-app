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

package com.scouts.kitchenplaner.utils

/**
 * A Stack is a mutable data structures which allows adding objects and removing them in a FILO
 * manner. Also grants access to the last element that was added to the stack
 */
class Stack<T> {
    private val _deque = ArrayDeque<T>()

    /**
     * Adds an element to the top of the stack
     *
     * @param e The element to be added
     */
    fun push(e: T) = _deque.addLast(e)

    /**
     * Removes and returns the topmost element of the stack
     *
     * @return The popped element
     */
    fun pop() = _deque.removeLast()

    /**
     * Returns the topmost element of the stack with out removing it
     *
     * @return The topmost element of the stack
     */
    fun top() = _deque.last()

    /**
     * @return True iff e is contained in the stack at any position
     */
    fun contains(e: T) = _deque.contains(e)

    /**
     * All elements currently contained in the stack
     */
    val content: List<T>
        get() = _deque.toList()
}