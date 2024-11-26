package com.example.tickectbusbooking_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tickectbusbooking_app.database.BusDao
import com.example.tickectbusbooking_app.database.RouteDao
import com.example.tickectbusbooking_app.model.Bus
import com.example.tickectbusbooking_app.model.Route
import com.example.tickectbusbooking_app.repository.BusRepository
import com.example.tickectbusbooking_app.repository.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusScheduleViewModel @Inject constructor(
    private val busRepository: BusRepository,
    private val routeRepository: RouteRepository,
    private val routeDao: RouteDao, // Inject RouteDao
    private val busDao: BusDao
) : ViewModel() {

    // State to hold routes
    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> get() = _routes

    // State to hold bus schedules
    private val _busSchedules = MutableStateFlow<List<Bus>>(emptyList())
    val busSchedules: StateFlow<List<Bus>> get() = _busSchedules

    init {
        fetchRoutes()
    }

    private fun fetchRoutes() {
        viewModelScope.launch {
            try {
                routeDao.getAllRoutes().collect { routeList ->
                    _routes.value = routeList
                    Log.d("BusScheduleViewModel", "Fetched routes: ${routeList.size} routes loaded.")
                }
            } catch (e: Exception) {
                Log.e("BusScheduleViewModel", "Error fetching routes: ${e.message}")
            }
        }
    }



    // State to track operation status (loading, success, or error)
    private val _operationStatus = MutableStateFlow<OperationStatus>(OperationStatus.Idle)
    val operationStatus: StateFlow<OperationStatus> get() = _operationStatus

    suspend fun searchBuses(origin: String, destination: String, travelDate: String) {
        viewModelScope.launch {
            _operationStatus.value = OperationStatus.Loading
            try {
                val route = routeRepository.getRouteByOriginAndDestination(origin, destination)
                if (route != null) {
                    val buses = busRepository.getBusesByRouteAndDate(route.id, travelDate)
                    _busSchedules.value = buses
                    _operationStatus.value = OperationStatus.Success
                } else {
                    _busSchedules.value = emptyList()
                    _operationStatus.value = OperationStatus.Error("No route found for the specified origin and destination.")
                }
            } catch (e: Exception) {
                _operationStatus.value = OperationStatus.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }


    // Sealed class for operation status
    sealed class OperationStatus {
        object Idle : OperationStatus()
        object Loading : OperationStatus()
        object Success : OperationStatus()
        data class Error(val message: String) : OperationStatus()
    }
}
