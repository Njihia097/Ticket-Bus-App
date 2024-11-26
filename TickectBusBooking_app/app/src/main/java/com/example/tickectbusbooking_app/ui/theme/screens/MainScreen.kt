package com.example.tickectbusbooking_app.ui.theme.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.tickectbusbooking_app.viewModel.BusScheduleViewModel
import com.example.tickectbusbooking_app.viewModel.SeatSelectionViewModel
import com.example.tickectbusbooking_app.viewModel.TicketSummaryViewModel
import com.example.tickectbusbooking_app.viewModel.UserViewModel
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun MainScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    busScheduleViewModel: BusScheduleViewModel = hiltViewModel(),
    seatSelectionViewModel: SeatSelectionViewModel = hiltViewModel(),
    ticketSummaryViewModel: TicketSummaryViewModel = hiltViewModel(),
    userId: Int // Pass authenticated user's ID
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "register"
    ) {
        // Login Screen
        composable("login") {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSuccess = {
                    navController.navigate("busSchedule") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // Registration Screen
        composable("register") {
            RegistrationScreen(
                userViewModel = userViewModel,
                onNavigateToLogin = { navController.navigate("login") }
            )
        }
        // Bus Schedule Screen
        composable("busSchedule") {
            BusScheduleScreen(
                busScheduleViewModel = busScheduleViewModel,
                onBusSelected = { busId, routeId ->
                    // Navigate to SeatSelectionScreen with selected busId and routeId
                    navController.navigate("seatSelection/$busId/$routeId")
                }
            )
        }

        // Seat Selection Screen
        composable(
            route = "seatSelection/{busId}/{routeId}",
            arguments = listOf(
                navArgument("busId") { type = NavType.IntType },
                navArgument("routeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            // Extract busId and routeId from navigation arguments
            val busId = backStackEntry.arguments?.getInt("busId")
            val routeId = backStackEntry.arguments?.getInt("routeId")
            if (busId != null && routeId != null) {
                SeatSelectionScreen(
                    seatSelectionViewModel = seatSelectionViewModel,
                    busId = busId,
                    routeId = routeId,
                    userId = userId,
                    onBookingConfirmed = { bookingId ->
                        // Navigate to BookingConfirmationScreen with bookingId
                        navController.navigate("bookingConfirmation/$bookingId")
                    }
                )
            } else {
                // Handle invalid navigation arguments
                Text("Invalid bus or route selection.")
            }
        }

        // Booking Confirmation Screen
        composable(
            route = "bookingConfirmation/{bookingId}",
            arguments = listOf(
                navArgument("bookingId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            // Extract bookingId from navigation arguments
            val bookingId = backStackEntry.arguments?.getInt("bookingId")
            if (bookingId != null) {
                BookingConfirmationScreen(
                    ticketSummaryViewModel = ticketSummaryViewModel,
                    bookingId = bookingId,
                    onNavigateHome = {
                        // Navigate back to BusScheduleScreen
                        navController.navigate("busSchedule") {
                            popUpTo("busSchedule") { inclusive = true }
                        }
                    }
                )
            } else {
                // Handle invalid bookingId
                Text("Invalid booking ID.")
            }
        }
    }
}
