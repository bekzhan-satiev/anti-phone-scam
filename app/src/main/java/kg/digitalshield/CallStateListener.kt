package kg.digitalshield

import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresPermission
import kg.digitalschield.R
import kg.digitalshield.api.AnalyzeApi
import kg.digitalshield.api.CheckApi
import kg.digitalshield.db.Call
import kg.digitalshield.db.CallRepository
import kg.digitalshield.db.CallStatus
import kg.digitalshield.dto.request.AnalyzeRequest
import kg.digitalshield.dto.request.CheckRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import java.io.DataOutputStream
import java.io.IOException
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class CallStateListener @Inject constructor(
    private val context: Context,
    private val callRepository: CallRepository,
    private val analyzeApi: AnalyzeApi,
) : PhoneStateListener() {

    @Volatile
    private var phone: String? = null

    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    private lateinit var model: Model
    private lateinit var recognizer: Recognizer

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    @Volatile
    private var isSuspiciousCall: Boolean? = null

    // Audio configuration
    private val sampleRate = 16000 // Vosk typically uses 16kHz
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var isInCall: Boolean = false

    init {
        initModel()
    }


    private fun initModel() {
        StorageService.unpack(
            context, "model-ru", "model",
            { model: Model? ->
                model?.let {
                    this.model = it
                    try {
                        recognizer = Recognizer(it, sampleRate.toFloat())
                    } catch (e: IOException) {
                        Log.e("RecognizerError", "Failed to create recognizer", e)
                    }
                }
            },
            { exception: IOException ->
                Log.e("ModelError", "Failed to unpack the model: ${exception.message}")
            }
        )
    }

    @RequiresPermission(
        allOf = [
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE
        ]
    )
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        this.phone = phoneNumber
        when (state) {
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                isInCall = true
                startRecording()
            }

            TelephonyManager.CALL_STATE_IDLE -> {
                isInCall = false

                phone?.let {
                    if (isSuspiciousCall == null && it.isNotEmpty() ) {
                        coroutineScope.launch {
                            val call = Call(
                                phoneNumber = it,
                                callDate = Date(),
                                callStatus = CallStatus.SAFE,
                                suspiciousPhrases = ""
                            )
                            callRepository.save(call)
                        }
                    }
                }

                isSuspiciousCall = null
                stopRecording()
            }

            TelephonyManager.CALL_STATE_RINGING -> {
                Log.d("CallState", "Ringing: $phone")
            }
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecording() {
        if (!isRecording && ::recognizer.isInitialized) {
            try {
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.VOICE_DOWNLINK, // Use VOICE_DOWNLINK for call audio
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize * 2
                )

                audioRecord?.startRecording()
                isRecording = true

                Thread {
                    val buffer = ByteArray(bufferSize)
                    while (isRecording) {
                        val bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                        if (bytesRead > 0) {
                            if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                                // Process final result
                                val result = recognizer.finalResult
                                Log.d("Final", "Final result: $result")
                                val text = extractContentFrom(result);
                                sendToAnalysis(phone!!, text)
                            }
                        }
                    }
                }.start()

            } catch (e: Exception) {
                Log.e("RecordingError", "Error starting recording", e)
                stopRecording()
            }
        }
    }

    private fun extractContentFrom(jsonString: String): String {
        return try {
            // Deserialize the JSON string into a RecognitionResult object
            val result = Json.decodeFromString<RecognitionResult>(jsonString)
            result.text
        } catch (e: Exception) {
            Log.e("JsonError", "Failed to parse JSON string: $jsonString", e)
            throw RuntimeException("Hey bitch!")
        }
    }

    private fun sendToAnalysis(phoneNumber: String, text: String) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                if (!isActive) return@launch // Check if coroutine is still active

                val request = AnalyzeRequest(phoneNumber, text)
                Log.d("Request is", request.toString())

                val response = analyzeApi.analyzePhrase(request)

                if (!isActive) return@launch // Check again after network call

                if (response.isSuccessful) {
                    val analysisResults = response.body()
                    Log.d("Analysis", "Success: $analysisResults")

                    analysisResults?.let { results ->
                        if (results.isNotEmpty()) {

                            // Mark as suspicious call
                            isSuspiciousCall = true

                            val call = Call(
                                phoneNumber = phoneNumber,
                                callDate = Date(),
                                callStatus = CallStatus.SUSPICIOUS,
                                suspiciousPhrases = results.joinToString(",")
                            )

                            callRepository.save(call)
                            Log.d("Added", "Suspicious call from $phone was added to the app")

                            stopRecording()

                            job.cancelChildren()

                            playScaryTone()
                        }
                    }
                } else {
                    Log.e("Analysis", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Log.d("Analysis", "Analysis cancelled")
                } else {
                    Log.e("Analysis", "Network error", e)
                }
            }
        }
    }


    private fun playScaryTone() {
        val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        while (isInCall) {
            toneGen.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_SSL, 200)
            Thread.sleep(200) // Wait until the tone finishes
        }
        toneGen.release()

    }

    private fun stopRecording() {
        if (isRecording) {
            isRecording = false
            phone = null
            try {
                audioRecord?.apply {
                    stop()
                    release()
                }
                audioRecord = null
                recognizer.reset() // Reset the recognizer for future use
            } catch (e: Exception) {
                Log.e("RecordingError", "Error stopping recording", e)
            }
        }
    }


    /**
     * Cleanup method to release resources.
     */
    fun cleanup() {
        coroutineScope.cancel() // Cancel the coroutine scope
        stopRecording() // Ensure recording is stopped
    }

    @Serializable
    private data class RecognitionResult(val text: String)

}