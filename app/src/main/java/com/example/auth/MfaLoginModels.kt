package com.example.auth

import java.util.UUID

/**
 * Step 41A - Hardened MFA Login Challenge Architecture Models
 * Implements defense-in-depth & attack-surface reduction for multi-factor authentication.
 */

enum class MfaFactorType {
    TOTP,
    BIOMETRIC,
    HARDWARE_KEY
}

enum class MfaChallengeStatus {
    PENDING,
    VERIFIED,
    EXPIRED,
    ATTEMPTS_EXCEEDED,
    REVOKED
}

/**
 * Step 41A - Primary MFA Login Challenge Request.
 * Issued after initial primary credential validation.
 */
data class MfaChallengeRequest(
    val userId: String,
    val preferredFactor: MfaFactorType = MfaFactorType.TOTP,
    val deviceFingerprintHash: String
)

/**
 * Step 41A - Short-lived signed MFA Challenge Token returned to client.
 * Does NOT contain any raw MFA secrets or seeds.
 */
data class MfaChallengeResponse(
    val challengeId: String = UUID.randomUUID().toString(),
    val challengeToken: String, // Cryptographically signed short-lived challenge token
    val factorType: MfaFactorType = MfaFactorType.TOTP,
    val maskedIdentity: String, // e.g. "m***@domain.com"
    val expiresAtEpochMs: Long,
    val attemptsRemaining: Int = 3
)

/**
 * Step 41A - MFA Verification submission request.
 * Submits time-bound TOTP code or biometric attestation bound to the challenge token.
 */
data class MfaVerificationRequest(
    val challengeId: String,
    val challengeToken: String,
    val verificationCode: String, // 6-digit TOTP or biometric assertion payload
    val clientTimestampMs: Long = System.currentTimeMillis()
)

/**
 * Step 41A - Result of MFA verification with generic error feedback to prevent timing/enum enumeration.
 */
data class MfaVerificationResult(
    val success: Boolean,
    val status: MfaChallengeStatus,
    val sessionAuthToken: String? = null,
    val attemptsRemaining: Int,
    val genericErrorMessage: String? = null
)

/**
 * Step 41A - Hardened In-Memory/Encrypted MFA Challenge Session State
 */
data class MfaChallengeSession(
    val challengeId: String,
    val userId: String,
    val signedChallengeToken: String,
    val status: MfaChallengeStatus = MfaChallengeStatus.PENDING,
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val expiresAtEpochMs: Long = System.currentTimeMillis() + (5 * 60 * 1000), // 5 minute max lifetime
    var failedAttempts: Int = 0,
    val maxAttempts: Int = 3
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiresAtEpochMs

    val isLockedOut: Boolean
        get() = failedAttempts >= maxAttempts
}
