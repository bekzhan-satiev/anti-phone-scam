package kg.digitalshield.db

import androidx.lifecycle.LiveData
import javax.inject.Inject

class CallRepository @Inject constructor(
    private val callDao: CallDao
) {
    fun getAllCalls(): LiveData<List<Call>> = callDao.getCallsOrderedByTime()

    suspend fun save(call: Call) {
        callDao.save(call)
    }

}