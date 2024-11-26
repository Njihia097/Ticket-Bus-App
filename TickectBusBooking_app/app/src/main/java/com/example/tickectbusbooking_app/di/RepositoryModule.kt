package com.example.tickectbusbooking_app.di

import com.example.tickectbusbooking_app.database.*
import com.example.tickectbusbooking_app.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // Provide UserRepository
    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    // Provide BusRepository
    @Provides
    @Singleton
    fun provideBusRepository(busDao: BusDao): BusRepository {
        return BusRepository(busDao)
    }

    // Provide RouteRepository
    @Provides
    @Singleton
    fun provideRouteRepository(routeDao: RouteDao): RouteRepository {
        return RouteRepository(routeDao)
    }

    // Provide SeatRepository
    @Provides
    @Singleton
    fun provideSeatRepository(seatDao: SeatDao): SeatRepository {
        return SeatRepository(seatDao)
    }

    // Provide BookingRepository
    @Provides
    @Singleton
    fun provideBookingRepository(bookingDao: BookingDao): BookingRepository {
        return BookingRepository(bookingDao)
    }
}
