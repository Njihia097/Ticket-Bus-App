package com.example.tickectbusbooking_app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tickectbusbooking_app.model.Bus
import kotlinx.coroutines.flow.Flow

@Dao
interface BusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bus: Bus): Long

    @Query("SELECT * FROM buses WHERE id = :busId")
    suspend fun getBusById(busId: Int): Bus?

    @Query("SELECT * FROM buses WHERE routeId = :routeId")
    suspend fun getBusesByRouteId(routeId: Int): List<Bus>

    @Query("SELECT * FROM buses WHERE routeId = " +
            ":routeId AND availableDates LIKE '%' || :travelDate || '%'")
    suspend fun getBusesByRouteIdAndDate(routeId: Int, travelDate: String): List<Bus>

    @Query("SELECT COUNT(*) FROM buses")
    suspend fun getBusCount(): Int

}

