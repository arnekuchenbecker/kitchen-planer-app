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

package com.scouts.kitchenplaner.datalayer.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.scouts.kitchenplaner.datalayer.dtos.MealIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.PersonNumberChangeIdentifierDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectArchivedDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectDataVersionDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectDatesDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectIdDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectImageDTO
import com.scouts.kitchenplaner.datalayer.dtos.ProjectStubDTO
import com.scouts.kitchenplaner.datalayer.entities.MealEntity
import com.scouts.kitchenplaner.datalayer.entities.PersonNumberChangeEntity
import com.scouts.kitchenplaner.datalayer.entities.ProjectEntity
import com.scouts.kitchenplaner.datalayer.entities.UserEntity
import com.scouts.kitchenplaner.datalayer.entities.UserProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDAO {
    @Transaction
    suspend fun createProject(
        project: ProjectEntity,
        meals: List<MealEntity>
    ): Long {
        val rowId = insertProject(project)
        val projectId = getProjectIdByRowId(rowId)

        meals.forEach {
            it.projectId = projectId
            insertMealEntity(it)
        }

        return projectId
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProject(entity: ProjectEntity): Long

    /**
     * Deletes the project with the given id
     *
     * @param id The ID of the project that should be deleted
     */
    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMealEntity(entity: MealEntity): Long

    @Query(
        "UPDATE meals " +
                "SET `order` = `order` + 1 " +
                "WHERE projectId = :projectId " +
                "AND `order` >= :index"
    )
    suspend fun increaseMealOrder(projectId: Long, index: Int)

    @Query(
        "UPDATE meals " +
                "SET `order` = `order` - 1 " +
                "WHERE projectId = :projectId " +
                "AND `order` >= :index"
    )
    suspend fun decreaseMealOrder(projectId: Long, index: Int)

    @Query("UPDATE projects " +
            "SET name = :name " +
            "WHERE id = :projectId")
    suspend fun changeProjectName(projectId: Long, name: String)

    @Delete(MealEntity::class)
    suspend fun deleteMeal(meal: MealIdentifierDTO)

    @Query("SELECT `order` FROM meals WHERE projectId = :projectId AND name = :name")
    suspend fun getMealOrder(projectId: Long, name: String): Int

    @Query("SELECT * FROM projects WHERE projects.id = :id")
    fun getProjectById(id: Long): Flow<ProjectEntity>

    /**
     * Searches for a project with the given online ID
     *
     * @param onlineID The online ID to search for
     * @return Whether a project with the given online ID exists
     */
    @Query("SELECT EXISTS(SELECT id FROM projects WHERE onlineID = :onlineID)")
    suspend fun existsProjectByOnlineID(onlineID: Long) : Boolean

    /**
     * Get a project entity from the data base. Does not push any further updates.
     *
     * @param id The id of the queried project
     * @return The project with the given id
     */
    @Query("SELECT * FROM projects WHERE projects.id = :id")
    suspend fun getCurrentProjectById(id: Long): ProjectEntity

    @Query("SELECT name FROM meals WHERE meals.projectId = :id ORDER BY meals.`order`")
    fun getMealsByProjectID(id: Long): Flow<List<String>>

    /**
     * Gets all meals currently in a project
     *
     * @param id The ID of the project
     * @return The meals currently in the project with the given ID
     */
    @Query("SELECT name FROM meals WHERE meals.projectId = :id ORDER BY meals.`order`")
    suspend fun getCurrentMealsByProjectID(id: Long): List<String>

    @Query("SELECT * FROM personNumberChanges WHERE personNumberChanges.projectId = :id")
    fun getPersonNumberChangesByProjectID(id: Long): Flow<List<PersonNumberChangeEntity>>

    /**
     * Gets all number changes relevant for a project
     *
     * @param id The ID of the project
     * @return A list of the PersonNumberChangeEntities relevant for the project
     */
    @Query("SELECT * FROM personNumberChanges WHERE projectId = :id")
    suspend fun getCurrentPersonNumberChangesByProjectID(id: Long) : List<PersonNumberChangeEntity>

    @Query("SELECT id FROM projects WHERE rowId = :rowId")
    suspend fun getProjectIdByRowId(rowId: Long): Long

    @Query(
        "SELECT projects.name AS name, projects.id AS id, projects.imageUri AS imageUri " +
                "FROM projects INNER JOIN userprojects ON projects.id = userprojects.projectId " +
                "WHERE userprojects.username = :username AND projects.isArchived = 0"
    )
    fun getProjectsForUser(username: String): Flow<List<ProjectStubDTO>>

    @Query(
        "SELECT projects.name AS name, projects.id AS id, projects.imageUri AS imageUri " +
                "FROM projects"
    )
    fun getAllProjectStubs(): Flow<List<ProjectStubDTO>>

    @Query("SELECT * FROM projects WHERE name = :projectName")
    suspend fun getProjectByProjectName(projectName: String): ProjectEntity

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    suspend fun getExistsUserByName(username: String): Int

    @Insert
    suspend fun insertUser(user: UserEntity)

    @Update(entity = ProjectEntity::class)
    suspend fun updateImage(image: ProjectImageDTO)

    @Update(entity = ProjectEntity::class)
    suspend fun updateDates(dates: ProjectDatesDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonNumberChange(entity: PersonNumberChangeEntity)

    @Delete(PersonNumberChangeEntity::class)
    suspend fun deletePersonNumberChange(identifierDTO: PersonNumberChangeIdentifierDTO)

    @Insert
    suspend fun addUserToProject(user: UserProjectEntity)

    @Delete
    suspend fun removeUserFromProject(user: UserProjectEntity)

    @Update
    suspend fun updateLastShownProjectForUser(entity: UserProjectEntity)

    @Query("SELECT * FROM userprojects WHERE username = :user ORDER BY lastShown DESC LIMIT :limit")
    fun getLatestShownProjectsForUser(user: String, limit: Int): Flow<List<UserProjectEntity>>

    // Methods for data management with the server
    /**
     * Gets the current data version number of the given project
     *
     * @param id The ID of the project
     * @return The data version
     */
    @Query("SELECT dataVersion FROM projects WHERE id = :id")
    suspend fun getCurrentProjectVersionNumberByID(id: Long) : Long

    /**
     * Gets the current image version number of the given project
     *
     * @param id The ID of the project
     * @return The image version
     */
    @Query("SELECT imageVersion FROM projects WHERE id = :id")
    suspend fun getCurrentImageVersionNumberByID(id: Long) : Long

    /**
     * Updates the data version number by applying the given DTO
     *
     * @param dto A DTO containing project ID and the updated version number
     */
    @Update(ProjectEntity::class)
    suspend fun updateVersionNumber(dto: ProjectDataVersionDTO)

    /**
     * Gets the current image URI for the given project
     *
     * @param id The ID of the project
     * @return The image URI
     */
    @Query("SELECT imageURI FROM projects WHERE id = :id")
    suspend fun getCurrentImageURIByID(id: Long) : String

    /**
     * Converts the given onlineID to a local ID
     *
     * @param onlineID The onlineID that should be converted
     * @return The local ID associated with the given onlineID
     */
    @Query("SELECT id FROM projects WHERE onlineID = :onlineID")
    suspend fun getCurrentProjectIDByOnlineID(onlineID: Long) : Long

    /**
     * Converts the given local ID to an onlineID
     *
     * @param id The local ID that should be converted
     * @return The onlineID associated with the given local ID
     */
    @Query("SELECT onlineID FROM projects WHERE id = :id")
    suspend fun getCurrentOnlineIDByProjectID(id: Long) : Long

    /**
     * Initializes a project for online use by setting the onlineID and initializing image and data
     * version numbers to 0
     *
     * @param projectID The local ID of the project that should be initialized
     * @param onlineID The onlineID the project is known by on the server
     */
    @Query("UPDATE projects SET onlineID = :onlineID, dataVersion = 0, imageVersion = 0 WHERE id = :projectID")
    suspend fun initializeOnlineProject(projectID: Long, onlineID: Long)

    // Methods for archiving projects

    @Delete(MealEntity::class)
    suspend fun deleteMealsByProjectId(projectId: ProjectIdDTO)

    @Update(ProjectEntity::class)
    suspend fun setProjectArchivedStatus(projectArchived: ProjectArchivedDTO)

    /**
     * Checks whether a project is archived
     *
     * @param id The ID of the project
     * @return Whether the project is archived
     */
    @Query("SELECT isArchived FROM projects WHERE id = :id")
    suspend fun isProjectArchived(id: Long) : Boolean
}
