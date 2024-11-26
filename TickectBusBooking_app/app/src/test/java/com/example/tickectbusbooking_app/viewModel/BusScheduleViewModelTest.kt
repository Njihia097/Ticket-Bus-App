package com.example.tickectbusbooking_app.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.tickectbusbooking_app.model.Bus
import com.example.tickectbusbooking_app.model.Route
import com.example.tickectbusbooking_app.repository.BusRepository
import com.example.tickectbusbooking_app.repository.RouteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BusScheduleViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val busRepository: BusRepository = mockk()
    private val routeRepository: RouteRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: BusScheduleViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set the test dispatcher
        viewModel = BusScheduleViewModel(busRepository, routeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset Dispatchers.Main after the test
    }

//    @Test
//    fun `searchBuses returns buses for valid route`() = runTest {
//        val mockRoute = Route(1, "City A", "City B", "3 hours", 50.0)
//        val mockBuses = listOf(
//            Bus(1, "Bus A", "AB123", 50, mockRoute.id),
//            Bus(2, "Bus B", "CD456", 50, mockRoute.id)
//        )
//
//        coEvery { routeRepository.getRouteByOriginAndDestination("City A", "City B") } returns mockRoute
//        coEvery { busRepository.getBusesByRouteId(mockRoute.id) } returns mockBuses
//
//        viewModel.searchBuses("City A", "City B", "2024-11-25")
//        testDispatcher.scheduler.advanceUntilIdle() // Ensure all coroutines complete
//
//        // Verify results
//        Assert.assertEquals(mockBuses, viewModel.busSchedules.first())
//        Assert.assertTrue(viewModel.operationStatus.first() is BusScheduleViewModel.OperationStatus.Success)
//    }

    @Test
    fun `searchBuses returns empty for invalid route`() = runTest {
        coEvery { routeRepository.getRouteByOriginAndDestination("City A", "City C") } returns null

        viewModel.searchBuses("City A", "City C", "2024-11-25")
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify results
        Assert.assertEquals(emptyList<Bus>(), viewModel.busSchedules.first())
        Assert.assertTrue(viewModel.operationStatus.first() is BusScheduleViewModel.OperationStatus.Error)
    }

    @Test
    fun `searchBuses handles exception gracefully`() = runTest {
        coEvery { routeRepository.getRouteByOriginAndDestination("City A", "City B") } throws Exception("Database error")

        viewModel.searchBuses("City A", "City B", "2024-11-25")
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify error handling
        Assert.assertEquals(emptyList<Bus>(), viewModel.busSchedules.first())
        val status = viewModel.operationStatus.first() as BusScheduleViewModel.OperationStatus.Error
        Assert.assertEquals("Database error", status.message)
    }
}
