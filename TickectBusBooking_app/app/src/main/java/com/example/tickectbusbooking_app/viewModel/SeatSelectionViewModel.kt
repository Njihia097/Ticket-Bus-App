package com.example.tickectbusbooking_app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tickectbusbooking_app.model.Booking
import com.example.tickectbusbooking_app.model.Seat
import com.example.tickectbusbooking_app.repository.BookingRepository
import com.example.tickectbusbooking_app.repository.RouteRepository
import com.example.tickectbusbooking_app.repository.SeatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SeatSelectionViewModel @Inject constructor(
    private val seatRepository: SeatRepository,
    private val routeRepository: RouteRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _seats = MutableStateFlow<List<Seat>>(emptyList())
    val seats: StateFlow<List<Seat>> = _seats

    private val _selectedSeats = MutableStateFlow<MutableList<Int>>(mutableListOf())
    val selectedSeats: StateFlow<List<Int>> = _selectedSeats

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    private var routePrice: Double = 0.0

    // Load seats and calculate route price based on selected bus and route
    fun initialize(busId: Int, routeId: Int) {
        viewModelScope.launch {
            _seats.value = seatRepository.getSeatsByBusId(busId)
            routePrice = routeRepository.getRoutePrice(routeId)
            calculateTotalPrice()
        }
    }

    // Handle seat selection
    fun selectSeat(seatId: Int) {
        if (!_selectedSeats.value.contains(seatId)) {
            _selectedSeats.value.add(seatId)
            updateSeatStatus(seatId, "Selected")
            calculateTotalPrice()
        }
    }

    // Handle seat deselection
    fun deselectSeat(seatId: Int) {
        if (_selectedSeats.value.contains(seatId)) {
            _selectedSeats.value.remove(seatId)
            updateSeatStatus(seatId, "Available")
            calculateTotalPrice()
        }
    }

    // Update seat status in the seat list
    private fun updateSeatStatus(seatId: Int, status: String) {
        val updatedSeats = _seats.value.map { seat ->
            if (seat.id == seatId) seat.copy(status = status) else seat
        }
        _seats.value = updatedSeats
    }

    // Calculate total price based on selected seats and route price
    private fun calculateTotalPrice() {
        _totalPrice.value = _selectedSeats.value.size * routePrice
    }

    // Confirm booking and save data
    fun confirmBooking(userId: Int) {
        viewModelScope.launch {
            _selectedSeats.value.forEach { seatId ->
                val booking = Booking(
                    userId = userId,
                    seatId = seatId,
                    bookingDate = getCurrentDateTime(),
                    status = "Confirmed",
                    totalPrice = routePrice
                )
                bookingRepository.insertBooking(booking)

                // Update seat status in the database
                updateSeatStatus(seatId, "Booked")
                seatRepository.updateSeatStatus(seatId, "Booked")
            }
            // Clear selected seats
            _selectedSeats.value.clear()
            calculateTotalPrice()
        }
    }

    // Get the current date and time
    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
