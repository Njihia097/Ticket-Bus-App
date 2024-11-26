package com.example.tickectbusbooking_app.repository

import com.example.tickectbusbooking_app.database.BookingDao
import com.example.tickectbusbooking_app.model.Booking

class BookingRepository(
    private val bookingDao: BookingDao
) {
    suspend fun insertBooking(booking: Booking): Long = bookingDao.insert(booking)

    suspend fun getBookingsByUserId(userId: Int):
            List<Booking> = bookingDao.getBookingsByUserId(userId)

    suspend fun getBookingsBySeatId(seatId: Int):
            List<Booking> = bookingDao.getBookingsBySeatId(seatId)

    suspend fun updateBookingStatus(
        bookingId: Int, status: String) = bookingDao.updateBookingStatus(bookingId, status)

    suspend fun getBookingById(bookingId: Int): Booking? = bookingDao.getBookingById(bookingId)

}