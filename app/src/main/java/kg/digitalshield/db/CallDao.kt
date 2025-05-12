package kg.digitalshield.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CallDao {

    @Upsert
    suspend fun save(call: Call)

    @Query("SELECT * FROM call WHERE id = :id")
    suspend fun getById(id: Int): Call

    @Query("SELECT * FROM call ORDER BY callDate DESC")
    fun getCallsOrderedByTime(): LiveData<List<Call>>

}