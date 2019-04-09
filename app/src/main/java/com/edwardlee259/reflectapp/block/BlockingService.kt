package com.edwardlee259.reflectapp.block

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.edwardlee259.reflectapp.R
import com.edwardlee259.reflectapp.ui.fill.SurveyActivity
import com.edwardlee259.reflectapp.utils.UsageStatsPermissions
import java.text.DateFormat
import java.util.*

class BlockingService : Service() {
    companion object {
        val LOG_TAG = BlockingService::class.java.simpleName

        const val PRIMARY_CHANNEL_ID = "blocking_notification_channel"
        const val NOTIFICATION_ID = 1349

        const val ACTION_BEGIN_BLOCKING = "com.edwardlee259.reflectapp.ACTION_BEGIN_BLOCKING"
        const val ACTION_POSTPONE = "com.edwardlee259.reflectapp.ACTION_POSTPONE"
        const val ACTION_STOP_BLOCKING = "com.edwardlee259.reflectapp.ACTION_STOP_BLOCKING"

        private val POSTPONE_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES
        private val POSTPONE_LIMIT = 4
        private val LOOP_INTERVAL = 5000L
    }

    // SharedPreference
    private lateinit var mCurrSharedPreferences: SharedPreferences

    // Blocking Management
    private var blockingEnabled: Boolean = false // general "should we block or not"
    private lateinit var mNotifyManager: NotificationManager
    private lateinit var mAlarmManager: AlarmManager
    private var nextBlockingTime: Int = 0 // general time of when blocking starts

    // Currently Blocking
    private val mHandler = Handler()
    private var currentlyBlocking: Boolean = false
    private var lastPostponeTime: Long? =
        null // when the last postpone was, or null if no postpones occurred
    private var nPostpones: Int = 0 // number of postpones

    override fun onCreate() {
        super.onCreate()

        Log.d(LOG_TAG, "creating blocking service")

        // set up Notifications and AlarmManager for daily alarms to begin blocking
        createNotificationChannel()
        mAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        mCurrSharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this.applicationContext)
        mCurrSharedPreferences.edit().putBoolean(getString(R.string.pref_key_service_status), false)
            .apply()

        blockingEnabled =
            mCurrSharedPreferences.getBoolean(getString(R.string.pref_key_blocking), false)
        nextBlockingTime =
            mCurrSharedPreferences.getInt(getString(R.string.pref_key_blocking_start_time), 0)
        if (blockingEnabled) {
            resetAlarms(nextBlockingTime)
        }
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "destroying service")
        disableBlocking()
        mCurrSharedPreferences.edit().putBoolean(getString(R.string.pref_key_service_status), true)
            .apply()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "start command sent with action: ${intent?.action}")
        when (intent?.action) {
            ACTION_BEGIN_BLOCKING -> {
                beginBlockingIfNeeded()
            }
            ACTION_POSTPONE -> {
                postponeBlocking()
            }
            ACTION_STOP_BLOCKING -> {
                stopBlockingIfNeeded()
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getServiceIntent(action: String, noCreate: Boolean): PendingIntent? {
        val intent = Intent(this, BlockingService::class.java)
        intent.action = action
        val flags = if (noCreate) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_NO_CREATE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getService(this, NOTIFICATION_ID, intent, flags)
    }

    private fun getReflectIntent(): Intent {
        val intent = Intent(this, SurveyActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }

    private fun resetAlarms(minutesAfterMidnight: Int) {
        val pendingIntent = getServiceIntent(ACTION_BEGIN_BLOCKING, false)
        mAlarmManager.cancel(pendingIntent)
        if (blockingEnabled) {
            val currDate = Calendar.getInstance()
            val nextDate = currDate.clone() as Calendar
            nextDate.set(Calendar.HOUR_OF_DAY, minutesAfterMidnight / 60)
            nextDate.set(Calendar.MINUTE, minutesAfterMidnight % 60)
            if (nextDate.before(currDate)) {
                nextDate.set(Calendar.DAY_OF_YEAR, currDate.get(Calendar.DAY_OF_YEAR) + 1)
            }

            val toPrint = DateFormat.getDateTimeInstance().format(nextDate.time)
            Log.d(LOG_TAG, "Creating alarms for $toPrint")
            val builder = getNotificationBuilder()
                .setAutoCancel(true)
                .setContentText("Next alarm at $toPrint")
            mNotifyManager.notify(NOTIFICATION_ID, builder.build())

            mAlarmManager.setRepeating(
                AlarmManager.RTC,
                nextDate.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    private fun beginBlockingIfNeeded() {
        Log.d(LOG_TAG, "beginning blocking if needed")
        if (blockingEnabled && !currentlyBlocking) {
            nPostpones = 0
            lastPostponeTime = null
            currentlyBlocking = true
            val builder = getNotificationBuilder()
                .setContentText("Blocking, tap to reflect now")
                .addAction(
                    R.drawable.ic_postpone,
                    "Postpone ($POSTPONE_LIMIT left)",
                    getServiceIntent(ACTION_POSTPONE, false)
                )
                .setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        0,
                        getReflectIntent(),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
            startForeground(NOTIFICATION_ID, builder.build())
            val runnable = object : Runnable {
                override fun run() {
                    if (shouldBlock()) {
                        val foregroundApp =
                            getForegroundApp(this@BlockingService, 4 * LOOP_INTERVAL)
                        Log.d(LOG_TAG, "Foreground app detected: $foregroundApp")
                        when {
                            foregroundApp == null -> {
                            }
                            foregroundApp == packageName -> {
                                Log.d(LOG_TAG, "It's our package")
                            }
                            isSystemPackage(foregroundApp) -> {
                                Log.d(LOG_TAG, "It's a system app")
                            }
                            isHomePackage(foregroundApp) -> {
                                Log.d(LOG_TAG, "It's the home package")
                            }
                            // TODO add whitelist
                            else -> {
                                Log.d(LOG_TAG, "We want to block this, I think")
                                val goToHome = Intent(Intent.ACTION_MAIN)
                                goToHome.addCategory(Intent.CATEGORY_HOME)
                                goToHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(goToHome)
                            }
                        }
                    }
                    mHandler.postDelayed(this, LOOP_INTERVAL)
                }
            }
            mHandler.postDelayed(runnable, 0)
        }
    }

    private fun isSystemPackage(packageName: String): Boolean {
        return (packageManager.getPackageInfo(
            packageName,
            0
        ).applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 1
    }

    private fun isHomePackage(packageName: String): Boolean {
        val checkHomeIntent = Intent(Intent.ACTION_MAIN)
        checkHomeIntent.addCategory(Intent.CATEGORY_HOME)
        val homePackage =
            packageManager.resolveActivity(checkHomeIntent, PackageManager.MATCH_DEFAULT_ONLY)
                .activityInfo.packageName
        return packageName == homePackage
    }

    private fun createNotificationChannel() {
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notifChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Notification Channel for indicating blocking status",
                NotificationManager.IMPORTANCE_HIGH
            )
            notifChannel.enableLights(true)
            notifChannel.lightColor = Color.RED
            notifChannel.enableVibration(true)
            notifChannel.description = "Primary notification channel"
            mNotifyManager.createNotificationChannel(notifChannel)
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_block)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setColor(ContextCompat.getColor(this.applicationContext, R.color.colorPrimary))
    }

    private fun postponeBlocking() {
        assert(currentlyBlocking)
        val builder = getNotificationBuilder()
            .setContentText("Blocking, tap to reflect now")

        // postpone blocking
        if (nPostpones < POSTPONE_LIMIT) {
            nPostpones++
            lastPostponeTime = System.currentTimeMillis()
        }

        // update notification to reflect postpones left
        val postponeUntilStr =
            DateFormat.getTimeInstance().format(Date(lastPostponeTime!! + POSTPONE_INTERVAL))
        builder.setContentText("Postponed until $postponeUntilStr")
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    getReflectIntent(),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        if (nPostpones < POSTPONE_LIMIT) {
            builder
                .addAction(
                    R.drawable.ic_postpone,
                    "Postpone (${POSTPONE_LIMIT - nPostpones} left)",
                    getServiceIntent(ACTION_POSTPONE, false)
                )
        } else {
            builder.setSubText("No more postpones")
        }
        mNotifyManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun disableBlocking() {
        Log.d(LOG_TAG, "disabling blocking")
        val intent = getServiceIntent(ACTION_BEGIN_BLOCKING, true)
        if (intent != null) {
            mAlarmManager.cancel(intent)
        }
        blockingEnabled = false
        stopBlockingIfNeeded()
    }

    private fun stopBlockingIfNeeded() {
        mHandler.removeCallbacksAndMessages(null)
        currentlyBlocking = false
        lastPostponeTime = null
        nPostpones = 0
        stopForeground(false)

        val builder = getNotificationBuilder()
            .setContentText("Thanks for reflecting! See you tomorrow.")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_done)
        mNotifyManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun shouldBlock(): Boolean {
        return (currentlyBlocking && (lastPostponeTime == null ||
                lastPostponeTime!! + POSTPONE_INTERVAL < System.currentTimeMillis()))
    }

    private fun getForegroundApp(context: Context, interval: Long): String? {
        val appContext = context.applicationContext
        return if (UsageStatsPermissions.hasUsageStatsPermission(appContext)) {
            val usm = appContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val events = usm.queryEvents(time - interval, time)
            val event = UsageEvents.Event()
            var foregroundApp: String? = null
            while (events.getNextEvent(event)) {
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    foregroundApp = event.packageName
                }
            }
            foregroundApp
        } else null
    }
}