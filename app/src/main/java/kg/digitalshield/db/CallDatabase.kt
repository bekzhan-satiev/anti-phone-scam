package kg.digitalshield.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Call::class], version = 1)
@TypeConverters(Converters::class)
abstract class CallDatabase: RoomDatabase() {

    companion object {
        const val NAME = "CallsDB"
    }

    abstract fun getCallDao() : CallDao

}