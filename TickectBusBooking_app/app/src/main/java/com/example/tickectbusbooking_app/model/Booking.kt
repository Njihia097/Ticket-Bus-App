package com.example.tickectbusbooking_app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val seatId: Int,
    val bookingDate: String,
    val status: String,
    val totalPrice: Double
)
