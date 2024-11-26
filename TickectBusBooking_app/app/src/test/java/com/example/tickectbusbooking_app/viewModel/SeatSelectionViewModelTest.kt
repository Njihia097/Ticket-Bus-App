package com.example.tickectbusbooking_app.viewModel

import com.example.tickectbusbooking_app.model.Booking
import com.example.tickectbusbooking_app.model.Seat
import com.example.tickectbusbooking_app.repository.BookingRepository
import com.example.tickectbusbooking_app.repository.RouteRepository
import com.example.tickectbusbooking_app.repository.SeatRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SeatSelectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var seatRepository: SeatRepository
    private lateinit var routeRepository: RouteRepository
    private lateinit var bookingRepository: BookingRepository
    private lateinit var viewModel: SeatSelectionViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // Override Dispatchers.Main
        seatRepository = mockk(relaxed = true)
        routeRepository = mockk()
        bookingRepository = mockk(relaxed = true)
        viewModel = SeatSelectionViewModel(seatRepository, routeRepository, bookingRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset Dispatchers.Main
    }

    @Test
    fun `test total price calculation`() = runTest {
        // Mock repository calls
        coEvery { routeRepository.getRoutePrice(1) } returns 100.0
        coEvery { seatRepository.getSeatsByBusId(101) } returns listOf(
            Seat(id = 1, busId = 101, seatNumber = "A1", status = "Available"),
            Seat(id = 2, busId = 101, seatNumber = "A2", status = "Available")
        )

        // Load seats and advance dispatcher
        viewModel.loadSeats(busId = 101, routeId = 1)
        advanceUntilIdle() // Ensure coroutines complete

        // Select seats
        viewModel.selectSeat(1)
        viewModel.selectSeat(2)

        // Assert total price
        assertEquals(200.0, viewModel.totalPrice.value, 0.0)
    }

    @Test
    fun `test seat selection and deselection updates status`() = runTest {
        // Mock repository calls
        coEvery { routeRepository.getRoutePrice(1) } returns 100.0
        coEvery { seatRepository.getSeatsByBusId(101) } returns listOf(
            Seat(id = 1, busId = 101, seatNumber = "A1", status = "Available")
        )

        // Load seats and advance dispatcher
        viewModel.loadSeats(busId = 101, routeId = 1)
        advanceUntilIdle() // Ensure coroutines complete

        // Select a seat
        viewModel.selectSeat(1)

        // Assert seat status is updated
        assertEquals("Selected", viewModel.seats.value.find { it.id == 1 }?.status)

        // Deselect the seat
        viewModel.deselectSeat(1)
        assertEquals("Available", viewModel.seats.value.find { it.id == 1 }?.status)
    }

    @Test
    fun `test booking confirmation updates seat status`() = runTest {
        // Mock repository calls
        coEvery { routeRepository.getRoutePrice(1) } returns 100.0
        coEvery { seatRepository.getSeatsByBusId(101) } returns listOf(
            Seat(id = 1, busId = 101, seatNumber = "A1", status = "Available")
        )
        coEvery { bookingRepository.insertBooking(any()) } returns 1L
        coEvery { seatRepository.updateSeatStatus(any(), any()) } returns Unit

        // Load seats and advance dispatcher
        viewModel.loadSeats(busId = 101, routeId = 1)
        advanceUntilIdle() // Ensure coroutines complete

        // Select a seat
        viewModel.selectSeat(1)

        // Confirm booking and advance dispatcher
        viewModel.confirmBooking(userId = 123)
        advanceUntilIdle() // Ensure coroutines complete

        // Verify booking and status updates
        coVerify { bookingRepository.insertBooking(any()) }
        coVerify { seatRepository.updateSeatStatus(1, "Booked") }

        // Assert seat status is updated in the ViewModel
        assertEquals("Booked", viewModel.seats.value.find { it.id == 1 }?.status)
    }
}
