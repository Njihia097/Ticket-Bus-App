package com.example.tickectbusbooking_app.viewModel

import com.example.tickectbusbooking_app.model.Booking
import com.example.tickectbusbooking_app.model.Route
import com.example.tickectbusbooking_app.model.Seat
import com.example.tickectbusbooking_app.repository.BookingRepository
import com.example.tickectbusbooking_app.repository.RouteRepository
import com.example.tickectbusbooking_app.repository.SeatRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TicketSummaryViewModelTest {

    private lateinit var bookingRepository: BookingRepository
    private lateinit var seatRepository: SeatRepository
    private lateinit var routeRepository: RouteRepository
    private lateinit var ticketSummaryViewModel: TicketSummaryViewModel
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        bookingRepository = mockk()
        seatRepository = mockk()
        routeRepository = mockk()
        ticketSummaryViewModel = TicketSummaryViewModel(
            bookingRepository,
            seatRepository,
            routeRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset Dispatchers.Main
    }

    @Test
    fun `test loadUserTickets`() = runTest {
        // Mock data
        val bookings = listOf(
            Booking(
                id = 1, userId = 1, seatId = 1, bookingDate = "2024-11-25",
                status = "Confirmed", totalPrice = 50.0
            ),
            Booking(
                id = 2, userId = 1, seatId = 2, bookingDate = "2024-11-25",
                status = "Confirmed", totalPrice = 50.0
            )
        )
        val seat1 = Seat(id = 1, busId = 1, seatNumber = "A1", status = "Booked")
        val seat2 = Seat(id = 2, busId = 1, seatNumber = "A2", status = "Booked")
        val route = Route(
            id = 1, origin = "City A", destination = "City B",
            duration = "2h", price = 50.0
        )

        coEvery { bookingRepository.getBookingsByUserId(1) } returns bookings
        coEvery { seatRepository.getSeatById(1) } returns seat1
        coEvery { seatRepository.getSeatById(2) } returns seat2
        coEvery { routeRepository.getRouteById(any()) } returns route

        // Perform the test
        ticketSummaryViewModel.loadUserTickets(1)

        // Allow coroutines to complete
        advanceUntilIdle()

        // Assert the results
        val tickets = ticketSummaryViewModel.tickets.value
        assertEquals(2, tickets.size)
        assertEquals("A1", tickets[0].seatNumbers.first())
        assertEquals("City A - City B", tickets[0].routeDetails)
        assertEquals(50.0, tickets[0].price, 0.0)
        assertEquals("Confirmed", tickets[0].status)
    }


    @Test
    fun `test cancelBooking`() = runTest {
        // Mock data
        val booking = Booking(
            id = 1, userId = 1, seatId = 1, bookingDate = "2024-11-25",
            status = "Confirmed", totalPrice = 50.0
        )

        coEvery { bookingRepository.getBookingById(1) } returns booking
        coEvery { bookingRepository.updateBookingStatus(1, "Cancelled") } just Runs
        coEvery { seatRepository.updateSeatStatus(1, "Available") } just Runs
        coEvery { bookingRepository.getBookingsByUserId(1) } returns emptyList()

        // Perform the test
        ticketSummaryViewModel.cancelBooking(1)

        // Allow coroutines to complete
        advanceUntilIdle()

        // Verify interactions
        coVerify { bookingRepository.updateBookingStatus(1, "Cancelled") }
        coVerify { seatRepository.updateSeatStatus(1, "Available") }
        assertEquals(emptyList<TicketSummaryViewModel.TicketSummary>(), ticketSummaryViewModel.tickets.value)
    }



    @Test
    fun `test markTicketAsUsed`() = runTest {
        // Mock data
        val booking = Booking(
            id = 1, userId = 1, seatId = 1, bookingDate = "2024-11-25",
            status = "Confirmed", totalPrice = 50.0
        )

        coEvery { bookingRepository.getBookingById(1) } returns booking
        coEvery { bookingRepository.updateBookingStatus(1, "Used") } just Runs
        coEvery { bookingRepository.getBookingsByUserId(1) } returns emptyList()

        // Perform the test
        ticketSummaryViewModel.markTicketAsUsed(1)

        // Allow coroutines to complete
        advanceUntilIdle()

        // Verify interactions
        coVerify { bookingRepository.updateBookingStatus(1, "Used") }
        assertEquals(emptyList<TicketSummaryViewModel.TicketSummary>(), ticketSummaryViewModel.tickets.value)
    }


}
