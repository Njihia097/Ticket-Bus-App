package com.example.tickectbusbooking_app.di

import android.content.Context
import androidx.room.Room
import com.example.tickectbusbooking_app.database.AppDatabase
import com.example.tickectbusbooking_app.database.BookingDao
import com.example.tickectbusbooking_app.database.BusDao
import com.example.tickectbusbooking_app.database.RouteDao
import com.example.tickectbusbooking_app.database.SeatDao
import com.example.tickectbusbooking_app.database.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideBookingDao(database: AppDatabase): BookingDao = database.bookingDao()

    @Provides
    fun provideBusDao(database: AppDatabase): BusDao = database.busDao()

    @Provides
    fun provideRouteDao(database: AppDatabase): RouteDao = database.routeDao()

    @Provides
    fun provideSeatDao(database: AppDatabase): SeatDao = database.seatDao()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}
