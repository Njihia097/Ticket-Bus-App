package com.example.tickectbusbooking_app.repository

import com.example.tickectbusbooking_app.database.BusDao
import com.example.tickectbusbooking_app.model.Bus

class BusRepository(
    private val busDao: BusDao
) {
    suspend fun insertBus(bus: Bus): Long = busDao.insert(bus)

    suspend fun getBusById(busId: Int): Bus? = busDao.getBusById(busId)

    suspend fun getBusesByRouteId(routeId: Int): List<Bus> = busDao.getBusesByRouteId(routeId)

    suspend fun getBusesByRouteAndDate(routeId: Int, travelDate: String): List<Bus> {
        return busDao.getBusesByRouteIdAndDate(routeId, travelDate)
    }

}