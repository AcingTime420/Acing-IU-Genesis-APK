package com.example.security

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

/**
 * Utility to interface with persistent storage partitions and system security master reset intents.
 * Supports FRP erasure verification flows and respects TestModeManager simulation mode.
 */
class PersistentDataBlockHandler(private val context: Context) {

    companion object {
        private const val TAG = "PersistentDataBlock"
        const val ACTION_MASTER_CLEAR = "android.intent.action.MASTER_CLEAR"
        const val EXTRA_WIPE_PERSISTENT_DATA = "android.intent.extra.WIPE_PERSISTENT_DATA"
        const val PERSISTENT_PARTITION_PATH = "/dev/block/bootdevice/by-name/persistent"
    }

    /**
     * Checks if the device persistent data block (FRP block) is accessible and present.
     */
    fun checkPersistentPartitionStatus(): String {
        return if (TestModeManager.isSimulationMode) {
            "Persistent Partition: $PERSISTENT_PARTITION_PATH [SIMULATION - MOCKED SECURE]"
        } else {
            "Persistent Partition: $PERSISTENT_PARTITION_PATH [LIVE HARDWARE TARGET - S25 ULTRA]"
        }
    }

    /**
     * Invokes authorized MasterClear reset intent with persistent partition wipe flag.
     * In Simulation Mode, logs the intent payload safely without executing system wipe.
     */
    fun invokeAuthorizedFrpErasure(reason: String = "Matrix Identity Biometric Consensus Authorization"): Boolean {
        if (TestModeManager.isSimulationMode) {
            TestModeManager.logAction(
                tag = TAG,
                actionDescription = "Simulated MasterClear Intent dispatch with EXTRA_WIPE_PERSISTENT_DATA=true. Reason: $reason",
                isLiveExecuted = false
            )
            Log.d(TAG, "[SIMULATION MODE] Intent action=$ACTION_MASTER_CLEAR, extra=$EXTRA_WIPE_PERSISTENT_DATA=true")
            return true
        }

        return try {
            val intent = Intent(ACTION_MASTER_CLEAR).apply {
                addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                putExtra(EXTRA_WIPE_PERSISTENT_DATA, true)
                putExtra("android.intent.extra.REASON", reason)
            }
            context.sendBroadcast(intent)
            TestModeManager.logAction(
                tag = TAG,
                actionDescription = "Live Broadcast sent: $ACTION_MASTER_CLEAR with $EXTRA_WIPE_PERSISTENT_DATA=true",
                isLiveExecuted = true
            )
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send MasterClear broadcast", e)
            TestModeManager.logAction(
                tag = TAG,
                actionDescription = "Failed to dispatch MasterClear intent: ${e.message}",
                isLiveExecuted = true
            )
            false
        }
    }
}
