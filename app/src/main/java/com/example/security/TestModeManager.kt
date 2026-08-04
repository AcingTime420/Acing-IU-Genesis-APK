package com.example.security

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton manager to toggle Simulation Mode vs Live Partition Execution.
 * Overrides partition writes, hardware triggers, and system resets with safe logging
 * so developers and auditors can safely test the Acing Matrix security framework on S25 Ultra hardware.
 */
object TestModeManager {
    private const val TAG = "TestModeManager"

    private val _isSimulationMode = MutableStateFlow(true)
    val isSimulationModeFlow: StateFlow<Boolean> = _isSimulationMode.asStateFlow()

    var isSimulationMode: Boolean
        get() = _isSimulationMode.value
        set(value) {
            _isSimulationMode.value = value
            Log.i(TAG, "Acing Matrix Security Execution Mode changed: SimulationMode = $value")
        }

    private val _simulationLogs = MutableStateFlow<List<String>>(emptyList())
    val simulationLogs: StateFlow<List<String>> = _simulationLogs.asStateFlow()

    fun logAction(tag: String, actionDescription: String, isLiveExecuted: Boolean) {
        val modePrefix = if (isLiveExecuted) "[LIVE EXECUTION]" else "[SIMULATION MODE]"
        val logEntry = "$modePrefix ($tag) $actionDescription"
        Log.i(TAG, logEntry)
        _simulationLogs.value = (_simulationLogs.value + logEntry).takeLast(50)
    }

    fun clearLogs() {
        _simulationLogs.value = emptyList()
    }
}
