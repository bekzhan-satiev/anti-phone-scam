package kg.digitalshield.db

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(callDate: Date): Long {
        return callDate.time
    }

    // Converter for CallStatus
    @TypeConverter
    fun fromCallStatus(callStatus: CallStatus): String {
        return callStatus.name // Convert enum to its name (e.g., "SAFE", "BLOCKED")
    }

    @TypeConverter
    fun toCallStatus(name: String): CallStatus {
        return CallStatus.valueOf(name) // Convert name back to enum
    }

}