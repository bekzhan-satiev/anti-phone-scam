package kg.digitalshield.db

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CallRepository @Inject constructor(
    private val callDao: CallDao
) {
    fun getAllCalls(): LiveData<List<Call>> = callDao.getCallsOrderedByTime()

    suspend fun add(call: Call) {
        callDao.save(call)
    }

    suspend fun deleteById(id: Int) {
        callDao.delete(id)
    }
}