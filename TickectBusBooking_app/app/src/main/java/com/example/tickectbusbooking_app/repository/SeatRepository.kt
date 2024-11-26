package com.example.tickectbusbooking_app.repository

import com.example.tickectbusbooking_app.database.SeatDao
import com.example.tickectbusbooking_app.model.Seat

class SeatRepository(
    private val seatDao: SeatDao
)
{
    suspend fun insertSeat(seat: Seat): Long = seatDao.insert(seat)

    suspend fun getSeatsByBusId(busId: Int): List<Seat> = seatDao.getSeatsByBusId(busId)

    suspend fun updateSeatStatus
                (seatId: Int, status: String) = seatDao.updateSeatStatus(seatId, status)

    suspend fun getSeatById(seatId: Int): Seat? = seatDao.getSeatById(seatId)

}