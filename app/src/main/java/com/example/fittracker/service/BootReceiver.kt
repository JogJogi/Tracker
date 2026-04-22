package com.example.fittracker.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fittracker.service.worker.StepOffsetResetWorker

/**
 * Resets the step counter offset on reboot so daily counts start from 0.
 * Does NOT start a Foreground Service directly (blocked on Android 15+).
 * Instead, delegates to WorkManager which will run asap.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return

        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<StepOffsetResetWorker>().build())
    }
}
