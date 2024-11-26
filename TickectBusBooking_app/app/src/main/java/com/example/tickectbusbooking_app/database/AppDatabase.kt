package com.example.tickectbusbooking_app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tickectbusbooking_app.model.Bus
import com.example.tickectbusbooking_app.model.Booking
import com.example.tickectbusbooking_app.model.Route
import com.example.tickectbusbooking_app.model.Seat
import com.example.tickectbusbooking_app.model.User

@Database
    (entities =
        [
        Bus::class,
        Booking::class,
        Route::class,
        Seat::class,
        User::class
        ],
        version = 1, exportSchema = false
    )
abstract class AppDatabase : RoomDatabase() {
    abstract fun busDao(): BusDao
    abstract fun bookingDao(): BookingDao
    abstract fun routeDao(): RouteDao
    abstract fun seatDao(): SeatDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addCallback(RoomDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class RoomDatabaseCallback : Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            db.execSQL("PRAGMA foreign_keys=ON;")
        }
    }
}
