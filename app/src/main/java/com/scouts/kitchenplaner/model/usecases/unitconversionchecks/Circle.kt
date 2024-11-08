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

package com.scouts.kitchenplaner.model.usecases.unitconversionchecks

class Circle<T : Any>(private val vertices: List<T>) {
    val length: Int
        get() = vertices.size

    constructor(vararg v: T) : this(v.toList())

    fun <S : Any> map(transform: (T) -> S) : Circle<S> {
        return Circle(vertices.map(transform))
    }

    override fun toString(): String {
        return vertices.toString()
    }

    override fun hashCode(): Int {
        return vertices.fold(0) { acc, t -> acc + t.hashCode() }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Circle<*>) {
            if (vertices.isEmpty()) {
                other.vertices.isEmpty()
            } else if (vertices.size != other.vertices.size) {
                false
            } else {
                val startIndex = vertices.indexOf(other.vertices[0])
                if (startIndex == -1) {
                    false
                } else {
                    var equal = true
                    for (i in vertices.indices) {
                        if (vertices[(startIndex + i) % vertices.size] != other.vertices[i]) {
                            equal = false
                        }
                    }
                    equal
                }
            }
        } else {
            false
        }
    }
}