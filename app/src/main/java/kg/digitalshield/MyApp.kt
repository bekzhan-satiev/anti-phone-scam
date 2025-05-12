package kg.digitalshield

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import org.vosk.Model
import org.vosk.android.StorageService
import java.io.IOException

@HiltAndroidApp
class MyApp : Application() {

    lateinit var voskModel: Model
        private set

    override fun onCreate() {
        super.onCreate()
        initVoskModel()
    }

    private fun initVoskModel() {
        try {
            StorageService.unpack(this, "model-ru", "model",
                { model ->
                    voskModel = model
                    Log.d("App", "Vosk model loaded successfully")
                },
                { e ->
                    Log.e("App", "Model unpack failed", e)
                    // Consider showing a warning to user
                }
            )
        } catch (e: IOException) {
            Log.e("App", "Model init failed", e)
        }
    }

    companion object {
        private const val TAG = "MyApp"
    }

}