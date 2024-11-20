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

package com.scouts.kitchenplaner.networklayer.kitchenplaner.services

import com.scouts.kitchenplaner.networklayer.kitchenplaner.dtos.projects.ServerProjectDTO
import com.scouts.kitchenplaner.networklayer.kitchenplaner.dtos.projects.ServerProjectStubDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for sending requests concerning projects to the server
 */
interface ProjectAPIService {
    /**
     * Gets all project stubs the given user is participating in
     *
     * @param username The user for which to query the project stubs
     * @return A list of all project stubs the user is participating in
     */
    @GET("/projects")
    suspend fun getProjectStubsByUsername(@Query("username") username: String) : List<ServerProjectStubDTO>

    /**
     * Creates a new project
     *
     * @param project The project that is to be created on the server
     * @return The id of the newly created project
     */
    @POST("/projects/create")
    suspend fun createNewProject(@Body project: ServerProjectDTO) : Long

    /**
     * Gets a project from the server
     *
     * @param id The online id of the requested project
     * @return The requested project
     */
    @GET("/projects/{id}")
    suspend fun getProject(@Path("id") id: Long) : ServerProjectDTO

    /**
     * Updates a project
     *
     * @param id The online id of the project that should be updated
     * @param project The project containing the updated data
     * @return A response object containing the new version number if the update was successful
     */
    @PUT("/projects/{id}")
    suspend fun updateProject(@Path("id") id: Long, @Body project: ServerProjectDTO) : Response<Long>
}