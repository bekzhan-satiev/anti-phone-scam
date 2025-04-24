package kg.digitalshield.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CallDao {

    @Upsert
    fun save(call: Call)

    @Query("DELETE FROM call WHERE id = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM call ORDER BY callDate DESC")
    fun getCallsOrderedByTime(): LiveData<List<Call>>

}