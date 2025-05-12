package kg.digitalshield

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kg.digitalshield.api.CheckApi
import kg.digitalshield.db.CallRepository
import kg.digitalshield.db.CallStatus
import kg.digitalshield.dto.request.CheckRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class FraudCallScreeningService : CallScreeningService() {

    @Inject
    lateinit var checkApi: CheckApi
    @Inject
    lateinit var callRepository: CallRepository

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onScreenCall(details: Call.Details) {
        val handle = details.handle ?: return
        val number = handle.schemeSpecificPart

        Log.d("CallScreening", "Incoming call from: $number")

        coroutineScope.launch {
            try {
                val request = CheckRequest(phone = number)
                val response = checkApi.checkForFraud(request)

                if (response.isSuccessful && response.body() == true) {
                    // Number is suspicious → REJECT CALL
                    Log.d("CallScreening", "Rejecting suspicious call from: $number")

                    val call = kg.digitalshield.db.Call(
                        phoneNumber = number,
                        callDate = Date(),
                        callStatus = CallStatus.BLOCKED,
                        suspiciousPhrases = ""
                    )
                    callRepository.save(call)

                    val rejection = CallResponse.Builder()
                        .setDisallowCall(true)
                        .setRejectCall(true)
                        .build()

                    respondToCall(details, rejection)


                }
            } catch (e: Exception) {
                Log.e("CallScreening", "Error checking fraud status", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}