package com.example.security

data class DeviceTelemetryInput(
    val hardwareId: String = "SM-S938U-VERIZON-01",
    val manufacturer: String = "Samsung",
    val modelCode: String = "SM-S938U",
    val androidVersion: String = "Android 15 (API 35)",
    val selinuxEnforcing: Boolean = true,
    val bootloaderLocked: Boolean = true,
    val partitionsUnmodified: Boolean = true,
    val knoxFuseIntact: Boolean = true,
    val isRooted: Boolean = false,
    val sha256Hash: String = "a1b2c3d4e5f67890123456789abcdef0123456789abcdef0123456789abcdef0",
    val securityPatchDate: String = "2026-07-01"
)

sealed class TelemetryValidationResult {
    data class Valid(val sanitizedInput: DeviceTelemetryInput) : TelemetryValidationResult()
    data class Invalid(val errors: List<String>, val isTamperAttempt: Boolean) : TelemetryValidationResult()
}

class TelemetryValidator {

    fun validate(input: DeviceTelemetryInput): TelemetryValidationResult {
        val errors = mutableListOf<String>()
        var isTamperAttempt = false

        // 1. Hardware ID Sanitization & Format Validation
        val sanitizedHardwareId = sanitizeString(input.hardwareId)
        if (sanitizedHardwareId.isBlank()) {
            errors.add("Hardware ID cannot be empty or contain only control characters.")
        } else if (sanitizedHardwareId.length < 4 || sanitizedHardwareId.length > 64) {
            errors.add("Hardware ID length must be between 4 and 64 characters.")
        } else if (!sanitizedHardwareId.matches(Regex("^[a-zA-Z0-9_\\-:\\.]+$"))) {
            errors.add("Hardware ID contains invalid characters (illegal symbols or script tags detected).")
            isTamperAttempt = true
        }

        if (sanitizedHardwareId.lowercase().contains("bypass") ||
            sanitizedHardwareId.lowercase().contains("spoof") ||
            sanitizedHardwareId.lowercase().contains("fake")
        ) {
            errors.add("Hardware ID contains known spoofing signature patterns.")
            isTamperAttempt = true
        }

        // 2. Manufacturer & Model Code Validation
        val sanitizedManufacturer = sanitizeString(input.manufacturer)
        val sanitizedModelCode = sanitizeString(input.modelCode)
        if (sanitizedManufacturer.isBlank()) {
            errors.add("Device manufacturer field is missing.")
        }
        if (sanitizedModelCode.isBlank()) {
            errors.add("Device model code field is missing.")
        }

        // 3. Cryptographic Hash Validation
        if (input.sha256Hash.isNotBlank()) {
            val hashPattern = Regex("^[a-fA-F0-9]{64}$")
            if (!hashPattern.matches(input.sha256Hash)) {
                errors.add("Partition SHA-256 hash digest is malformed (must be 64-char hex string).")
                isTamperAttempt = true
            }
        }

        // 4. Contradiction & Anomaly / Tamper Detection
        if (input.isRooted && input.knoxFuseIntact) {
            errors.add("Contradictory Telemetry: System claims root privileges active while Knox Warranty Fuse is reported 0x0 (intact).")
            isTamperAttempt = true
        }

        if (input.isRooted && input.bootloaderLocked) {
            errors.add("Contradictory Telemetry: Device reports rooted kernel with a locked bootloader without AVB key registration.")
            isTamperAttempt = true
        }

        if (!input.partitionsUnmodified && input.bootloaderLocked) {
            errors.add("Integrity Violation: Partition modifications detected while AVB bootloader claims Locked status.")
            isTamperAttempt = true
        }

        return if (errors.isEmpty()) {
            TelemetryValidationResult.Valid(
                input.copy(
                    hardwareId = sanitizedHardwareId,
                    manufacturer = sanitizedManufacturer,
                    modelCode = sanitizedModelCode
                )
            )
        } else {
            TelemetryValidationResult.Invalid(errors = errors, isTamperAttempt = isTamperAttempt)
        }
    }

    private fun sanitizeString(raw: String): String {
        return raw.replace(Regex("[<>&\"';=]"), "").trim()
    }
}
