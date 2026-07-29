package com.example.trust

import com.example.logging.CentralizedLoggingService
import com.example.security.DeviceTelemetryInput
import com.example.security.TelemetryValidationResult
import com.example.security.TelemetryValidator

enum class TrustTier(
    val label: String,
    val description: String,
    val colorHex: Long
) {
    TRUSTED("Trusted", "Zero-trust compliance verified. Full system access authorized.", 0xFF10B981),
    ELEVATED("Elevated", "Satisfies baseline security requirements. Standard operations active.", 0xFF3B82F6),
    RESTRICTED("Restricted", "Security degradation detected. Operations restricted to read-only diagnostics.", 0xFFF59E0B),
    QUARANTINED("Quarantined", "Security compromise, root, or tamper detected! Device isolated in zero-trust lockdown.", 0xFFEF4444)
}

data class SignalContribution(
    val name: String,
    val pointsAwarded: Int,
    val maxPoints: Int,
    val isActive: Boolean,
    val isPenalty: Boolean = false,
    val description: String
)

data class DeviceTrustReport(
    val score: Int,
    val tier: TrustTier,
    val isAuthorized: Boolean,
    val signals: List<SignalContribution>,
    val isValidated: Boolean,
    val validationErrors: List<String> = emptyList(),
    val isTamperDetected: Boolean = false,
    val correlationId: String
)

class DeviceTrustService(
    private val validator: TelemetryValidator = TelemetryValidator()
) {

    suspend fun calculateTrustReport(
        rawInput: DeviceTelemetryInput,
        loggingService: CentralizedLoggingService,
        operatorRole: String = "Principal Architect",
        correlationIdOverride: String? = null
    ): DeviceTrustReport {
        val cid = correlationIdOverride ?: loggingService.generateCorrelationId("TRST")

        // 1. Telemetry Validation Layer Check
        val validationResult = validator.validate(rawInput)

        if (validationResult is TelemetryValidationResult.Invalid) {
            loggingService.logTelemetrySubmission(
                hardwareId = rawInput.hardwareId,
                isValid = false,
                details = "Validation Failure: ${validationResult.errors.joinToString(" | ")}",
                role = operatorRole,
                correlationId = cid
            )

            val signals = listOf(
                SignalContribution("SELinux Enforcing", 0, 40, rawInput.selinuxEnforcing, description = "Mandatory Access Control"),
                SignalContribution("Bootloader Locked", 0, 30, rawInput.bootloaderLocked, description = "Hardware Root of Trust"),
                SignalContribution("Partitions Unmodified", 0, 20, rawInput.partitionsUnmodified, description = "AVB 2.0 DM-Verity Integrity"),
                SignalContribution("Knox Fuse Intact", 0, 10, rawInput.knoxFuseIntact, description = "Hardware Security Fuse"),
                SignalContribution("Root Detection Penalty", 0, -100, rawInput.isRooted, isPenalty = true, description = "Invalid Telemetry Data")
            )

            return DeviceTrustReport(
                score = 0,
                tier = TrustTier.QUARANTINED,
                isAuthorized = false,
                signals = signals,
                isValidated = false,
                validationErrors = validationResult.errors,
                isTamperDetected = validationResult.isTamperAttempt,
                correlationId = cid
            )
        }

        val sanitizedInput = (validationResult as TelemetryValidationResult.Valid).sanitizedInput

        // 2. Trust Score Formula Calculation
        var calculatedScore = 0

        val selinuxPts = if (sanitizedInput.selinuxEnforcing) 40 else 0
        val bootloaderPts = if (sanitizedInput.bootloaderLocked) 30 else 0
        val partitionPts = if (sanitizedInput.partitionsUnmodified) 20 else 0
        val knoxPts = if (sanitizedInput.knoxFuseIntact) 10 else 0

        calculatedScore = selinuxPts + bootloaderPts + partitionPts + knoxPts

        // Force to 0 if root detected
        val isRooted = sanitizedInput.isRooted
        if (isRooted) {
            calculatedScore = 0
        }

        // Tier Classification
        val tier = when {
            isRooted -> TrustTier.QUARANTINED
            calculatedScore >= 90 -> TrustTier.TRUSTED
            calculatedScore >= 70 -> TrustTier.ELEVATED
            calculatedScore >= 40 -> TrustTier.RESTRICTED
            else -> TrustTier.QUARANTINED
        }

        val isAuthorized = calculatedScore >= 80

        val signals = listOf(
            SignalContribution(
                name = "SELinux Policy (MAC)",
                pointsAwarded = selinuxPts,
                maxPoints = 40,
                isActive = sanitizedInput.selinuxEnforcing,
                description = "Enforcing state grants +40 pts"
            ),
            SignalContribution(
                name = "Bootloader Lock State",
                pointsAwarded = bootloaderPts,
                maxPoints = 30,
                isActive = sanitizedInput.bootloaderLocked,
                description = "Locked bootloader grants +30 pts"
            ),
            SignalContribution(
                name = "Partition Verification (AVB)",
                pointsAwarded = partitionPts,
                maxPoints = 20,
                isActive = sanitizedInput.partitionsUnmodified,
                description = "Unmodified DM-Verity image grants +20 pts"
            ),
            SignalContribution(
                name = "Knox Hardware Fuse",
                pointsAwarded = knoxPts,
                maxPoints = 10,
                isActive = sanitizedInput.knoxFuseIntact,
                description = "Intact hardware fuse grants +10 pts"
            ),
            SignalContribution(
                name = "Root Detection Guardrail",
                pointsAwarded = if (isRooted) -100 else 0,
                maxPoints = 0,
                isActive = isRooted,
                isPenalty = true,
                description = if (isRooted) "CRITICAL: Root detected! Forced score to 0." else "No root detected"
            )
        )

        val report = DeviceTrustReport(
            score = calculatedScore,
            tier = tier,
            isAuthorized = isAuthorized,
            signals = signals,
            isValidated = true,
            validationErrors = emptyList(),
            isTamperDetected = false,
            correlationId = cid
        )

        // 3. Audit Log capturing with correlation ID
        loggingService.logTelemetrySubmission(
            hardwareId = sanitizedInput.hardwareId,
            isValid = true,
            details = "Trust Score: ${report.score}/100 | Tier: ${tier.label} | Access Authorized: ${report.isAuthorized}",
            role = operatorRole,
            correlationId = cid
        )

        return report
    }
}
