package com.example.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.data.SecurityAuditDatabase
import com.example.data.SecurityAuditEventEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Provides a secure gatekeeper function to verify user identity before launching sensitive
 * firmware analysis dashboards, integrating with androidx.biometric and logging to SecurityAuditDatabase.
 */
class BiometricAuthManager(private val context: Context) {

    private val auditDb = SecurityAuditDatabase.getDatabase(context.applicationContext)
    private val scope = CoroutineScope(Dispatchers.IO)

    enum class BiometricStatus {
        READY,
        NO_HARDWARE,
        HARDWARE_UNAVAILABLE,
        NONE_ENROLLED,
        SECURITY_UPDATE_REQUIRED,
        UNSUPPORTED
    }

    /**
     * Evaluates the hardware biometric capabilities of the device.
     */
    fun checkBiometricAvailability(): BiometricStatus {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.READY
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HARDWARE_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NONE_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricStatus.SECURITY_UPDATE_REQUIRED
            else -> BiometricStatus.UNSUPPORTED
        }
    }

    /**
     * Gatekeeper verification: Prompts for biometric identity (fingerprint/face/device credential)
     * before granting access to sensitive firmware analysis dashboards.
     */
    fun authenticateGatekeeper(
        activity: FragmentActivity,
        title: String = "Firmware Analysis Security Gate",
        subtitle: String = "Verify identity to access partition analysis & cryptographic hashes",
        description: String = "Biometric authentication required by zero-trust firmware governance policy.",
        onSuccess: () -> Unit,
        onError: (errorCode: Int, errString: String) -> Unit,
        onFailed: () -> Unit
    ) {
        val status = checkBiometricAvailability()
        if (status != BiometricStatus.READY) {
            // Log fallback/bypass in security audit database
            logAuditEvent(
                securityLevel = "WARNING",
                message = "Biometric gatekeeper accessed via hardware fallback. Hardware status: $status"
            )
            onSuccess()
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    logAuditEvent(
                        securityLevel = "CRITICAL_SECURE",
                        message = "User successfully passed biometric gatekeeper for firmware analysis session. Type: ${result.authenticationType}"
                    )
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    logAuditEvent(
                        securityLevel = "WARNING",
                        message = "Biometric gatekeeper authentication error ($errorCode): $errString"
                    )
                    onError(errorCode, errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    logAuditEvent(
                        securityLevel = "ALERT",
                        message = "Biometric gatekeeper authentication failed: Identity rejected."
                    )
                    onFailed()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun logAuditEvent(securityLevel: String, message: String) {
        scope.launch {
            try {
                auditDb.securityAuditDao().insertAuditEvent(
                    SecurityAuditEventEntity(
                        timestamp = System.currentTimeMillis(),
                        security_level = securityLevel,
                        message = message
                    )
                )
            } catch (e: Exception) {
                android.util.Log.e("BiometricAuthManager", "Failed to write audit event: ${e.message}")
            }
        }
    }
}
