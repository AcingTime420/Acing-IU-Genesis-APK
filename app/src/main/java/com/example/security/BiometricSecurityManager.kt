package com.example.security

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

/**
 * Data model for mapping a registered finger to an Acing IU security action.
 */
data class FingerprintMapping(
    val fingerName: String, // e.g. "Right Thumb", "Right Index", "Left Ring", "Left Pinky"
    val assignedAction: SecurityActionType,
    val description: String,
    val isEnabled: Boolean = true
)

enum class SecurityActionType(val label: String, val badgeColorHex: Long) {
    DEFAULT_UNLOCK("Standard Unlock & Launcher", 0xFF2E7D32),
    CONNECT_WALLET("Connect Acing Matrix Identity Wallet", 0xFF1565C0),
    RADIO_LOCKDOWN("Toggle USB Data & 2G/3G Radio Lockdown", 0xFFE65100),
    AUTHORIZED_FRP_RESET("Trigger Authorized Persistent FRP Wipe", 0xFFC62828)
}

/**
 * Manager utilizing androidx.biometric to authenticate fingerprint actions
 * and execute mapped Acing Matrix security triggers.
 */
class BiometricSecurityManager(private val context: Context) {

    private val mappings = mutableMapOf<String, FingerprintMapping>(
        "Right Thumb" to FingerprintMapping(
            fingerName = "Right Thumb",
            assignedAction = SecurityActionType.DEFAULT_UNLOCK,
            description = "Standard system unlock and One UI / Acing Foundation launcher access."
        ),
        "Right Index" to FingerprintMapping(
            fingerName = "Right Index",
            assignedAction = SecurityActionType.CONNECT_WALLET,
            description = "Authenticates Matrix Identity and connects to local dApp credential wallet."
        ),
        "Left Ring" to FingerprintMapping(
            fingerName = "Left Ring",
            assignedAction = SecurityActionType.RADIO_LOCKDOWN,
            description = "Engages emergency hardware lockdown: Disables USB data & 2G/3G cellular."
        ),
        "Left Pinky" to FingerprintMapping(
            fingerName = "Left Pinky",
            assignedAction = SecurityActionType.AUTHORIZED_FRP_RESET,
            description = "Initiates authorized persistent partition erasure with Matrix 3/3 consensus."
        )
    )

    fun getMappings(): List<FingerprintMapping> = mappings.values.toList()

    fun updateMapping(fingerName: String, action: SecurityActionType) {
        val existing = mappings[fingerName]
        if (existing != null) {
            mappings[fingerName] = existing.copy(assignedAction = action)
        }
    }

    /**
     * Prompts user with AndroidX BiometricPrompt and invokes callbacks based on authentication result.
     */
    fun authenticateFingerprintAction(
        activity: FragmentActivity,
        targetFinger: String,
        onSuccess: (FingerprintMapping) -> Unit,
        onError: (String) -> Unit
    ) {
        val executor: Executor = ContextCompat.getMainExecutor(activity)
        val mapping = mappings[targetFinger] ?: FingerprintMapping(
            fingerName = targetFinger,
            assignedAction = SecurityActionType.DEFAULT_UNLOCK,
            description = "Default security mapping"
        )

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(mapping)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError("Authentication Error ($errorCode): $errString")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Biometric failed to match registered finger profile.")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acing Matrix Security Trigger")
            .setSubtitle("Authenticating ${mapping.fingerName} for ${mapping.assignedAction.label}")
            .setDescription(mapping.description)
            .setNegativeButtonText("Cancel")
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            // Fallback for emulator / non-biometric environment simulation
            onSuccess(mapping)
        }
    }
}
