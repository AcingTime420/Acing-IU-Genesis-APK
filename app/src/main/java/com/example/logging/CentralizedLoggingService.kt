package com.example.logging

import com.example.data.SecurityRepository
import java.util.UUID

class CentralizedLoggingService(private val repository: SecurityRepository) {

    private var activeWorkflowCorrelationId: String? = null

    fun generateCorrelationId(prefix: String = "CORR"): String {
        val shortUuid = UUID.randomUUID().toString().take(8)
        val timestamp = System.currentTimeMillis().toString().takeLast(6)
        return "$prefix-$shortUuid-$timestamp"
    }

    fun startWorkflow(workflowName: String): String {
        val correlationId = generateCorrelationId("WF")
        activeWorkflowCorrelationId = correlationId
        return correlationId
    }

    fun getActiveWorkflowCorrelationId(): String {
        return activeWorkflowCorrelationId ?: generateCorrelationId("OP")
    }

    fun clearWorkflow() {
        activeWorkflowCorrelationId = null
    }

    suspend fun logOperation(
        category: String,
        title: String,
        details: String,
        severity: String = "INFO",
        role: String = "Principal Architect",
        correlationId: String? = null,
        deviceId: String? = null,
        outcome: String = "SUCCESS"
    ): String {
        val cid = correlationId ?: activeWorkflowCorrelationId ?: generateCorrelationId("EVT")
        repository.logEvent(
            category = category,
            title = title,
            details = details,
            severity = severity,
            role = role,
            correlationId = cid,
            deviceId = deviceId ?: "LOCAL_DEVICE",
            outcome = outcome
        )
        return cid
    }

    suspend fun logDeviceAction(
        actionTitle: String,
        details: String,
        severity: String = "INFO",
        role: String = "Principal Architect",
        correlationId: String? = null,
        deviceId: String = "Pixel 9 Pro (Lab Node #1)"
    ): String {
        return logOperation(
            category = "Device Action",
            title = actionTitle,
            details = details,
            severity = severity,
            role = role,
            correlationId = correlationId,
            deviceId = deviceId
        )
    }

    suspend fun logResearchWorkflow(
        workflowName: String,
        stepTitle: String,
        details: String,
        severity: String = "INFO",
        role: String = "Principal Architect",
        correlationId: String? = null
    ): String {
        return logOperation(
            category = "Research Workflow",
            title = "[$workflowName] $stepTitle",
            details = details,
            severity = severity,
            role = role,
            correlationId = correlationId
        )
    }

    suspend fun logThreatAnalysis(
        scenario: String,
        outcomeDetails: String,
        severity: String = "INFO",
        role: String = "Principal Architect",
        correlationId: String? = null
    ): String {
        return logOperation(
            category = "Threat Analysis",
            title = "AI Threat Evaluation: ${scenario.take(30)}...",
            details = outcomeDetails,
            severity = severity,
            role = role,
            correlationId = correlationId
        )
    }

    suspend fun logTelemetrySubmission(
        hardwareId: String,
        isValid: Boolean,
        details: String,
        role: String = "Principal Architect",
        correlationId: String? = null
    ): String {
        return logOperation(
            category = "Telemetry Validation",
            title = if (isValid) "Telemetry Verified ($hardwareId)" else "Telemetry Rejected ($hardwareId)",
            details = details,
            severity = if (isValid) "SECURE" else "CRITICAL",
            role = role,
            correlationId = correlationId,
            deviceId = hardwareId,
            outcome = if (isValid) "VALIDATED" else "REJECTED"
        )
    }
}
