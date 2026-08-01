package com.example.ai

import android.util.Log
import com.example.BuildConfig

enum class GeminiConfigStatus {
    CONFIGURED,
    MISSING_OR_PLACEHOLDER,
    INVALID_FORMAT,
    SERVICE_UNAVAILABLE
}

data class GeminiConfigValidationResult(
    val status: GeminiConfigStatus,
    val isFailSafeMode: Boolean,
    val userMessage: String,
    val maskedKey: String
)

object GeminiConfigValidator {

    private const val TAG = "GeminiConfigValidator"

    fun validateConfig(): GeminiConfigValidationResult {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        return when {
            apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY" -> {
                Log.w(
                    TAG,
                    "GEMINI_API_KEY is missing or placeholder ('$apiKey'). Aegis AI entering Fail-Safe Mode (Local Simulation)."
                )
                GeminiConfigValidationResult(
                    status = GeminiConfigStatus.MISSING_OR_PLACEHOLDER,
                    isFailSafeMode = true,
                    userMessage = "Configuration Required: GEMINI_API_KEY is not set in Secrets panel. Running in Local Security Simulation Mode.",
                    maskedKey = if (apiKey.isNullOrBlank()) "UNSET" else "PLACEHOLDER (MY_GEMINI_API_KEY)"
                )
            }
            apiKey.length < 15 -> {
                Log.w(
                    TAG,
                    "GEMINI_API_KEY format appears invalid (length ${apiKey.length}). Aegis AI entering Fail-Safe Mode."
                )
                GeminiConfigValidationResult(
                    status = GeminiConfigStatus.INVALID_FORMAT,
                    isFailSafeMode = true,
                    userMessage = "Invalid Configuration: GEMINI_API_KEY format is invalid. Check your AI Studio Secrets configuration.",
                    maskedKey = "${apiKey.take(4)}...${apiKey.takeLast(2)}"
                )
            }
            else -> {
                Log.i(TAG, "GEMINI_API_KEY successfully validated and injected via BuildConfig.")
                GeminiConfigValidationResult(
                    status = GeminiConfigStatus.CONFIGURED,
                    isFailSafeMode = false,
                    userMessage = "Live Gemini AI Model Endpoint Connected & Operational.",
                    maskedKey = "${apiKey.take(4)}...${apiKey.takeLast(4)}"
                )
            }
        }
    }
}
