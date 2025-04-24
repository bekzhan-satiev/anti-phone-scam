package kg.digitalshield

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager

class BackgroundRecordingService : Service() {
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var callStateListener: CallStateListener

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create and start the foreground notification
        val notification = createNotification()
        startForeground(1, notification)

        // Initialize the telephony manager and listener
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        callStateListener = CallStateListener(this)
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE)

        return START_STICKY
    }

    override fun onDestroy() {
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(): Notification {
        val channelId = "recording_service_channel"
        val channelName = "Call Recording Service"

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)

        return Notification.Builder(this, channelId)
            .setContentTitle("Call Recording Service")
            .setContentText("Recording calls in progress...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }
}