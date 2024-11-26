package com.example.tickectbusbooking_app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "buses")
data class Bus(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val number: String, // Bus registration number
    val totalSeats: Int,
    val routeId: Int,
    val availableDates: String // Comma-separated dates
)
