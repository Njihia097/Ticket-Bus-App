package com.example.tickectbusbooking_app.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tickectbusbooking_app.model.Seat
import com.example.tickectbusbooking_app.viewModel.SeatSelectionViewModel

@Composable
fun SeatSelectionScreen(
    seatSelectionViewModel: SeatSelectionViewModel,
    busId: Int,
    routeId: Int,
    onBookingConfirmed: (bookingId: Int) -> Unit,
    userId: Int
) {
    // Initialize the ViewModel when the screen is loaded
    androidx.compose.runtime.LaunchedEffect(Unit) {
        seatSelectionViewModel.initialize(busId, routeId)
    }

    val seats by seatSelectionViewModel.seats.collectAsState()
    val selectedSeats by seatSelectionViewModel.selectedSeats.collectAsState()
    val totalPrice by seatSelectionViewModel.totalPrice.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        // Seat Map Grid
        Text(text = "Select Your Seats", style = MaterialTheme.typography.headlineSmall)
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(seats) { seat ->
                SeatItem(
                    seat = seat,
                    isSelected = selectedSeats.contains(seat.id),
                    onClick = {
                        if (seat.status == "Available") {
                            seatSelectionViewModel.selectSeat(seat.id)
                        } else if (seat.status == "Selected") {
                            seatSelectionViewModel.deselectSeat(seat.id)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Total Price
        Text(
            text = "Total Price: KES $totalPrice",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Confirm Booking Button
        Button(
            onClick = {
                seatSelectionViewModel.confirmBooking(userId)
                onBookingConfirmed(123) // Replace with real booking ID logic
            },
            enabled = selectedSeats.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Confirm Booking")
        }
    }
}


@Composable
fun SeatItem(seat: Seat, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .background(
                color = when (seat.status) {
                    "Available" -> if (isSelected) Color.Blue else Color.Gray
                    "Booked" -> Color.Red
                    else -> Color.Gray
                },
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(
                enabled = seat.status == "Available" || seat.status == "Selected",
                onClick = onClick
            )
    ) {
        Text(text = seat.seatNumber, color = Color.White)
    }
}
