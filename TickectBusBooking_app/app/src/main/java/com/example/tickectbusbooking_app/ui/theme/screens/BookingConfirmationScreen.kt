package com.example.tickectbusbooking_app.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tickectbusbooking_app.model.Booking
import com.example.tickectbusbooking_app.viewModel.TicketSummaryViewModel

@Composable
fun BookingConfirmationScreen(
    ticketSummaryViewModel: TicketSummaryViewModel,
    bookingId: Int,
    onNavigateHome: () -> Unit
) {
    val bookingDetails by ticketSummaryViewModel.bookingDetails.collectAsState()

    LaunchedEffect(bookingId) {
        ticketSummaryViewModel.loadBookingDetails(bookingId)
    }

    if (bookingDetails == null) {
        Text("Loading booking details...")
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Booking Confirmation",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Route: ${bookingDetails!!.routeDetails}")
            Text(text = "Seats: ${bookingDetails!!.seatNumbers.joinToString(", ")}")
            Text(text = "Price: KES ${bookingDetails!!.price}")
            Text(text = "Booking Date: ${bookingDetails!!.bookingDate}")
            Text(text = "Status: ${bookingDetails!!.status}")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNavigateHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }
        }
    }
}
