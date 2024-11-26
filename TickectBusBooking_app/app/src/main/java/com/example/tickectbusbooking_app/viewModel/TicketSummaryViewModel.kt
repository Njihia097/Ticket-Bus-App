package com.example.tickectbusbooking_app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tickectbusbooking_app.repository.BookingRepository
import com.example.tickectbusbooking_app.repository.RouteRepository
import com.example.tickectbusbooking_app.repository.SeatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketSummaryViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val seatRepository: SeatRepository,
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _tickets = MutableStateFlow<List<TicketSummary>>(emptyList())
    val tickets: StateFlow<List<TicketSummary>> = _tickets

    private val _operationStatus = MutableStateFlow<OperationStatus>(OperationStatus.Idle)
    val operationStatus: StateFlow<OperationStatus> = _operationStatus

    private val _bookingDetails = MutableStateFlow<BookingDetails?>(null)
    val bookingDetails: StateFlow<BookingDetails?> = _bookingDetails

    fun loadBookingDetails(bookingId: Int) {
        viewModelScope.launch {
            try {
                val booking = bookingRepository.getBookingById(bookingId)
                val seat = seatRepository.getSeatById(booking?.seatId ?: 0)
                val route = routeRepository.getRouteById(seat?.busId ?: 0)

                if (booking != null && seat != null && route != null) {
                    _bookingDetails.value = BookingDetails(
                        bookingId = booking.id,
                        seatNumbers = listOf(seat.seatNumber),
                        routeDetails = "${route.origin} - ${route.destination}",
                        price = route.price,
                        bookingDate = booking.bookingDate,
                        status = booking.status
                    )
                }
            } catch (e: Exception) {
                _bookingDetails.value = null
            }
        }
    }

    data class BookingDetails(
        val bookingId: Int,
        val seatNumbers: List<String>,
        val routeDetails: String,
        val price: Double,
        val bookingDate: String,
        val status: String
    )

    data class TicketSummary(
        val bookingId: Int,
        val seatNumbers: List<String>,
        val routeDetails: String,
        val price: Double,
        val bookingDate: String,
        val status: String
    )

    sealed class OperationStatus {
        object Idle : OperationStatus()
        object Loading : OperationStatus()
        data class Success(val message: String) : OperationStatus()
        data class Error(val message: String) : OperationStatus()
    }

    // Fetch all bookings for the logged-in user
    fun loadUserTickets(userId: Int) {
        viewModelScope.launch {
            _operationStatus.value = OperationStatus.Loading
            try {
                val bookings = bookingRepository.getBookingsByUserId(userId)
                val ticketSummaries = bookings.map { booking ->
                    val seat = seatRepository.getSeatById(booking.seatId)
                    val route = routeRepository.getRouteById(seat?.busId ?: 0)
                    TicketSummary(
                        bookingId = booking.id,
                        seatNumbers = listOfNotNull(seat?.seatNumber),
                        routeDetails = "${route?.origin} - ${route?.destination}",
                        price = route?.price ?: 0.0,
                        bookingDate = booking.bookingDate,
                        status = booking.status
                    )
                }
                _tickets.value = ticketSummaries
                _operationStatus.value = OperationStatus.Idle
            } catch (e: Exception) {
                _operationStatus.value =
                    OperationStatus.Error(e.message ?: "Failed to load tickets.")
            }
        }
    }

    // Cancel a booking
    fun cancelBooking(bookingId: Int) {
        viewModelScope.launch {
            _operationStatus.value = OperationStatus.Loading
            try {
                // Update booking status to Cancelled
                bookingRepository.updateBookingStatus(bookingId, "Cancelled")
                // Free the associated seat
                val booking = bookingRepository.getBookingById(bookingId)
                booking?.let {
                    seatRepository.updateSeatStatus(it.seatId, "Available")
                }
                _operationStatus.value = OperationStatus.Success("Booking cancelled successfully.")
                // Refresh tickets after cancellation
                loadUserTickets(booking?.userId ?: 0)
            } catch (e: Exception) {
                _operationStatus.value =
                    OperationStatus.Error(e.message ?: "Failed to cancel booking.")
            }
        }
    }

    // Optional: Mark ticket as Used
    fun markTicketAsUsed(bookingId: Int) {
        viewModelScope.launch {
            _operationStatus.value = OperationStatus.Loading
            try {
                bookingRepository.updateBookingStatus(bookingId, "Used")
                _operationStatus.value = OperationStatus.Success("Ticket marked as used.")
                // Refresh tickets
                val booking = bookingRepository.getBookingById(bookingId)
                loadUserTickets(booking?.userId ?: 0)
            } catch (e: Exception) {
                _operationStatus.value =
                    OperationStatus.Error(e.message ?: "Failed to update ticket status.")
            }
        }
    }
}
