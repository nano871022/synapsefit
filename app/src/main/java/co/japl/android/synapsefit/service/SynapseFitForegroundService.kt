@file:Suppress("MaxLineLength")

package co.japl.android.synapsefit.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import co.japl.android.synapsefit.MainActivity
import co.japl.android.synapsefit.R

class SynapseFitForegroundService : Service() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        val action = intent?.action
        if (action == ACTION_STOP) {
            stopForegroundService()
            return START_NOT_STICKY
        }

        val titleExtra = intent?.getStringExtra(EXTRA_TITLE) ?: getString(R.string.app_name)
        val isWorkout = action == ACTION_START_WORKOUT

        val notificationTitle =
            if (isWorkout) {
                getString(R.string.fgs_workout_title)
            } else {
                getString(R.string.fgs_llm_title)
            }

        val notificationText =
            if (isWorkout) {
                getString(R.string.fgs_workout_desc, titleExtra)
            } else {
                getString(R.string.fgs_llm_desc)
            }

        val contentIntent =
            Intent(this, MainActivity::class.java).apply {
                setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val serviceType =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                } else {
                    0
                }
            startForeground(NOTIFICATION_ID, notification, serviceType)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun stopForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel =
                NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "synapsefit_foreground_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START_WORKOUT = "co.japl.android.synapsefit.ACTION_START_WORKOUT"
        const val ACTION_START_LLM = "co.japl.android.synapsefit.ACTION_START_LLM"
        const val ACTION_STOP = "co.japl.android.synapsefit.ACTION_STOP"
        const val EXTRA_TITLE = "extra_title"

        fun startWorkoutService(
            context: Context,
            planTitle: String,
        ) {
            val intent =
                Intent(context, SynapseFitForegroundService::class.java).apply {
                    action = ACTION_START_WORKOUT
                    putExtra(EXTRA_TITLE, planTitle)
                }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun startLlmService(
            context: Context,
            taskTitle: String = "",
        ) {
            val intent =
                Intent(context, SynapseFitForegroundService::class.java).apply {
                    action = ACTION_START_LLM
                    putExtra(EXTRA_TITLE, taskTitle)
                }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent =
                Intent(context, SynapseFitForegroundService::class.java).apply {
                    action = ACTION_STOP
                }
            context.startService(intent)
        }
    }
}
