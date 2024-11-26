package com.example.tickectbusbooking_app.repository

import com.example.tickectbusbooking_app.database.RouteDao
import com.example.tickectbusbooking_app.model.Route
import kotlinx.coroutines.flow.Flow

class RouteRepository(
    private val routeDao: RouteDao
)
{
    suspend fun insertRoute(route: Route): Long = routeDao.insert(route)

    suspend fun getRouteById(routeId: Int): Route? = routeDao.getRouteById(routeId)

    suspend fun getRoutePrice(routeId: Int): Double {
        val route = routeDao.getRouteById(routeId)
        return route?.price ?: 0.0 // Default to 0.0 if the route is not found
    }

    suspend fun getRouteByOriginAndDestination(origin: String, destination: String): Route? {
        return routeDao.getRouteByOriginAndDestination(origin, destination)
    }

    fun getAllRoutes(): Flow<List<Route>> {
        return routeDao.getAllRoutes()
    }

}