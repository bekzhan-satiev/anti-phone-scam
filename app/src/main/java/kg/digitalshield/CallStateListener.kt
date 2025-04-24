package kg.digitalshield

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresPermission
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import java.io.IOException

class CallStateListener(private val context: Context) : PhoneStateListener() {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private lateinit var model: Model
    private lateinit var recognizer: Recognizer

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

    @RequiresPermission(allOf = [
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_PHONE_STATE
    ])
    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
        when (state) {
            TelephonyManager.CALL_STATE_OFFHOOK -> startRecording()
            TelephonyManager.CALL_STATE_IDLE -> stopRecording()
            TelephonyManager.CALL_STATE_RINGING -> Log.d("CallState", "Ringing: $phoneNumber")
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
                                val result = recognizer.result
                                Log.d("Recognition", "Final result: $result")
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
}