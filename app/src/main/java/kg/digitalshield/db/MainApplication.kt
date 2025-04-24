package kg.digitalshield.db

import android.app.Application
import androidx.room.Room

class MainApplication : Application() {

    companion object {
        lateinit var callDatabase: CallDatabase
    }

    override fun onCreate() {
        super.onCreate()
        callDatabase = Room.databaseBuilder(
            applicationContext,
            CallDatabase::class.java,
            CallDatabase.NAME)
            .fallbackToDestructiveMigration(true)
            .build()
    }
}