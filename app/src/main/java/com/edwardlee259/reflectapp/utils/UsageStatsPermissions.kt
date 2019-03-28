package com.edwardlee259.reflectapp.utils

import android.app.Activity
import android.app.AppOpsManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

class UsageStatsPermissions {

    companion object {
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
            if (hasUsageStatsPermission(activity.applicationContext)) return null
            return activity.let {
                // Use the Builder class for convenient dialog construction
                val builder = AlertDialog.Builder(it)
                builder.setMessage("This app requires usage stats to help keep you on task. This must be done by the user in settings.")
                    .setPositiveButton("Go to settings") { dialog, id ->
                        // FIRE ZE MISSILES!
                        requestUsageStatsPermission(activity.applicationContext)
                    }
                    .setNegativeButton("Cancel") { dialog, id ->
                        // User cancelled the dialog
                        activity.finishAndRemoveTask()
                    }
                    .setOnDismissListener {
                        activity.finishAndRemoveTask()
                    }
                // Create the AlertDialog object and return it
                builder.create()
            }
        }

    }
}