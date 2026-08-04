package com.example.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

/**
 * Biometric Authentication Manager utilizing androidx.biometric.
 * Handles Matrix Identity verification flow and binds hardware fingerprint sensors
 * on S25 Ultra hardware to Acing Matrix actions.
 */
class BiometricAuthManager(private val context: Context) {

    companion object {
        private const val TAG = "BiometricAuthManager"
    }

    private val biometricManager: BiometricManager = BiometricManager.from(context)

    /**
     * Checks if biometric hardware is available and enrolled.
     */
    fun canAuthenticate(): Int {
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
    }

    /**
     * Prompts the user for biometric authentication to verify Matrix Identity.
     */
    fun authenticateMatrixIdentity(
        activity: FragmentActivity,
        title: String = "Matrix Identity Verification",
        subtitle: String = "Hardware Biometric Binding",
        description: String = "Authenticate fingerprint to verify Matrix Identity for action authorization.",
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (String) -> Unit
    ) {
        val executor: Executor = ContextCompat.getMainExecutor(activity)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    TestModeManager.logAction(TAG, "Biometric authentication succeeded for Matrix Identity", isLiveExecuted = true)
                    onSuccess(result)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val msg = "Biometric Error ($errorCode): $errString"
                    TestModeManager.logAction(TAG, msg, isLiveExecuted = false)
                    onError(msg)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    val msg = "Biometric authentication failed. Fingerprint unaligned."
                    TestModeManager.logAction(TAG, msg, isLiveExecuted = false)
                    onError(msg)
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }
}
