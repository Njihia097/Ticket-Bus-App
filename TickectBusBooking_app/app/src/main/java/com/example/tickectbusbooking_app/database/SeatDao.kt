package com.example.tickectbusbooking_app.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tickectbusbooking_app.model.Seat

@Dao
interface SeatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(seat: Seat): Long

    @Query("SELECT * FROM seats WHERE busId = :busId")
    suspend fun getSeatsByBusId(busId: Int): List<Seat>

    @Query("UPDATE seats SET status = :status WHERE id = :seatId")
    suspend fun updateSeatStatus(seatId: Int, status: String)

    @Query("SELECT * FROM seats WHERE id = :seatId")
    suspend fun getSeatById(seatId: Int): Seat?

}