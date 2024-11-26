package com.example.tickectbusbooking_app.util

import com.example.tickectbusbooking_app.database.AppDatabase
import com.example.tickectbusbooking_app.database.BusDao
import com.example.tickectbusbooking_app.database.RouteDao
import com.example.tickectbusbooking_app.model.Bus
import com.example.tickectbusbooking_app.model.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

object DataInitializer {

    fun populateData(database: AppDatabase) {
        val routeDao = database.routeDao()
        val busDao = database.busDao()

        CoroutineScope(Dispatchers.IO).launch {
            // Check if routes already exist
            if (routeDao.getRouteCount() == 0) {
                populateRoutes(routeDao)
            }

            // Check if buses already exist
            if (busDao.getBusCount() == 0) {
                populateBuses(busDao, routeDao)
            }
        }
    }

    private suspend fun populateRoutes(routeDao: RouteDao) {
        val kenyanTowns = listOf("Nairobi", "Mombasa", "Kisumu", "Eldoret", "Nakuru", "Thika", "Nyeri", "Machakos", "Migori")
        val routes = mutableListOf<Route>()
        var routeId = 1
        for (i in kenyanTowns.indices) {
            for (j in kenyanTowns.indices) {
                if (i != j) {
                    val price = (500..3000).random().toDouble()
                    val duration = "${(3..10).random()} hours"
                    routes.add(
                        Route(
                            id = routeId++,
                            origin = kenyanTowns[i],
                            destination = kenyanTowns[j],
                            duration = duration,
                            price = price
                        )
                    )
                }
            }
        }
        routes.forEach { routeDao.insert(it) }
    }

    private suspend fun populateBuses(busDao: BusDao, routeDao: RouteDao) {
        val busNames = listOf(
            "Modern Coast", "Easy Coach", "Mash Poa", "Guardian Coach", "Dreamline Express",
            "Simba Coach", "Tahmeed Coach", "Classic Shuttle", "Coast Bus", "Transline Classic"
        )

        // Collect the Flow to get List<Route>
        val routes = routeDao.getAllRoutes().first()

        val buses = mutableListOf<Bus>()
        for (i in 1..15) {
            val name = busNames[i % busNames.size]
            val number = "K${(100..999).random()} ${('A'..'Z').random()}${('A'..'Z').random()}"
            val totalSeats = (50..85).random()
            val routeId = routes.random().id

            // Generate available dates as a comma-separated string
            val availableDates = (1..10).map {
                getRandomDateWithinNextMonth()
            }.distinct().joinToString(",")

            buses.add(
                Bus(
                    name = name,
                    number = number,
                    totalSeats = totalSeats,
                    routeId = routeId,
                    availableDates = availableDates
                )
            )
        }
        buses.forEach { busDao.insert(it) }
    }

    private fun getRandomDateWithinNextMonth(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, (1..30).random())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
