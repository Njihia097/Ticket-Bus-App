package com.example.tickectbusbooking_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.tickectbusbooking_app.ui.theme.TickectBusBooking_appTheme
import com.example.tickectbusbooking_app.ui.theme.screens.MainScreen
import com.example.tickectbusbooking_app.viewModel.BusScheduleViewModel
import com.example.tickectbusbooking_app.viewModel.SeatSelectionViewModel
import com.example.tickectbusbooking_app.viewModel.TicketSummaryViewModel
import com.example.tickectbusbooking_app.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Use Hilt to inject ViewModels
    private val busScheduleViewModel: BusScheduleViewModel by viewModels()
    private val seatSelectionViewModel: SeatSelectionViewModel by viewModels()
    private val ticketSummaryViewModel: TicketSummaryViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TickectBusBooking_appTheme {
                val authenticatedUserId = userViewModel.getAuthenticatedUserId() ?: -1 // Default to -1 if not authenticated
                MainScreen(
                    busScheduleViewModel = busScheduleViewModel,
                    seatSelectionViewModel = seatSelectionViewModel,
                    ticketSummaryViewModel = ticketSummaryViewModel,
                    userViewModel = userViewModel,
                    userId = authenticatedUserId
                )
            }
        }
    }
}
