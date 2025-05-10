package kg.digitalshield

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.ToneGenerator
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresPermission
import com.chaquo.python.Python
import kg.digitalshield.db.Call
import kg.digitalshield.db.CallRepository
import kg.digitalshield.db.CallStatus
import kg.digitalshield.db.RecognitionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import java.io.IOException
import java.util.Date

class CallStateListener(private val context: Context, private val callRepository: CallRepository) :
    PhoneStateListener() {

    private var phone: String? = null

    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    private lateinit var model: Model
    private lateinit var recognizer: Recognizer


    private val python = Python.getInstance()
    private val stringAnalyzerModule = python.getModule("phrases_analyzer")
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Audio configuration
    private val sampleRate = 16000 // Vosk typically uses 16kHz
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

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
            TelephonyManager.CALL_STATE_OFFHOOK -> startRecording()
            TelephonyManager.CALL_STATE_IDLE -> stopRecording()
            TelephonyManager.CALL_STATE_RINGING -> Log.d("CallState", "Ringing: $phone")
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

    private fun extractTextFromJson(jsonString: String): String {
        return try {
            // Deserialize the JSON string into a RecognitionResult object
            val result = Json.decodeFromString<RecognitionResult>(jsonString)
            result.text
        } catch (e: Exception) {
            Log.e("JsonError", "Failed to parse JSON string: $jsonString", e)
            throw RuntimeException("Hey bitch!")
        }
    }

    private fun stopRecording() {
        if (isRecording) {
            isRecording = false
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

    private fun processRecognitionResult(result: String) {
        coroutineScope.launch {
            analyzeStringInBackground(result)
        }
    }


    private suspend fun analyzeStringInBackground(result: String) = withContext(Dispatchers.IO) {
        try {
            val analysisResult = stringAnalyzerModule.callAttr("analyze_string", result)?.asList()
            Log.d("AnalysisResult", "Analysis result: $analysisResult")

            analysisResult?.let { list ->
                Log.d("PythonAnalyze", "Analyzed value: $list")

                // Log an error if score > 0.6 (no need for Main thread)
                if (analysisResult.isNotEmpty()) {
                    Log.e("HighRiskAlert", "Detected high-risk phrase! Score: $list")
                    phone?.let {
                        val call = Call(
                            phoneNumber = it,
                            callDate = Date(), // Current timestamp
                            callStatus = CallStatus.SUSPICIOUS, // Assuming CallStatus has a HIGH_RISK value
                            suspiciousPhrases = list.joinToString(",") // Store the detected phrase
                        )

                        callRepository.save(call)

                        Log.d("Added", "Suspecious call from $phone was added to the app")

                        stopRecording()

                        withContext(Dispatchers.Main) {
                            playBeepSound()
                        }
                    }
                }
            }

        } catch (e: Exception) {
            Log.e("PythonError", "Error calling Python analyze_string", e)
        }
    }

    private fun playBeepSound() {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            // Save the current notification volume
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)

            // Set the notification volume to maximum (optional, adjust as needed)
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, maxVolume, 0)

            // Initialize the ToneGenerator with STREAM_NOTIFICATION and default volume
            val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

            // Play the beep tone
            toneGenerator.startTone(ToneGenerator.TONE_SUP_ERROR, 3000) // 200ms duration
            toneGenerator.release()

            // Restore the original notification volume
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, currentVolume, 0)
        } catch (e: Exception) {
            Log.e("SoundError", "Failed to play beep sound", e)
        }
    }

    /**
     * Cleanup method to release resources.
     */
    fun cleanup() {
        coroutineScope.cancel() // Cancel the coroutine scope
        stopRecording() // Ensure recording is stopped
    }
}