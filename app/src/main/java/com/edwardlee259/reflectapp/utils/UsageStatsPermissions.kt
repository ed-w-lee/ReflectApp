package com.edwardlee259.reflectapp.utils

import android.app.Activity
import android.app.AppOpsManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog

class UsageStatsPermissions {

    companion object {
        private val LOG_TAG = UsageStatsPermissions::class.java.simpleName

        fun hasUsageStatsPermission(context: Context): Boolean {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOps.checkOpNoThrow(
                "android:get_usage_stats", android.os.Process.myUid(),
                context.packageName
            )
            return (mode == AppOpsManager.MODE_ALLOWED)
        }

        fun requestUsageStatsPermission(context: Context) {
            if (!hasUsageStatsPermission(context)) {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        }

        fun requestPermissionsDialog(activity: Activity): Dialog? {
            if (hasUsageStatsPermission(activity.applicationContext)) {
                Log.d(LOG_TAG, "we have permissions. don't present dialog")
                return null
            }
            return activity.let {
                // Use the Builder class for convenient dialog construction
                val builder = AlertDialog.Builder(it)
                builder.setMessage("This app requires usage stats to help keep you on task. This must be done by the user in settings.")
                    .setPositiveButton("Go to settings") { _, _ ->
                        // FIRE ZE MISSILES!
                        requestUsageStatsPermission(activity.applicationContext)
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        // User cancelled the dialog
                        activity.finishAndRemoveTask()
                    }
                // Create the AlertDialog object and return it
                builder.create()
            }
        }

    }
}