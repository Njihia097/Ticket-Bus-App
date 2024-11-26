package com.example.tickectbusbooking_app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seats")
data class Seat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val busId: Int,
    val seatNumber: String,
    val status: String, // Available, Reserved, or Booked
    val row: Int? = null,
    val column: Int? = null
)
