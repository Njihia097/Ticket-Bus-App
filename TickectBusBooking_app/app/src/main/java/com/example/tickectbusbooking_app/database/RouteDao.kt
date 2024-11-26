package com.example.tickectbusbooking_app.database

import android.adservices.adid.AdId
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tickectbusbooking_app.model.Route
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(route: Route): Long

    @Query("SELECT * FROM routes WHERE id = :routeId")
    suspend fun getRouteById(routeId: Int): Route?

    @Query("SELECT * FROM routes WHERE origin = :origin AND destination = :destination LIMIT 1")
    suspend fun getRouteByOriginAndDestination(origin: String, destination: String): Route?

    @Query("SELECT COUNT(*) FROM routes")
    suspend fun getRouteCount(): Int

    @Query("SELECT * FROM routes")
    fun getAllRoutes(): Flow<List<Route>>



}