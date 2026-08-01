package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class ApiKeyStatus {
    VALID_CONFIGURED,
    MISSING_OR_PLACEHOLDER,
    INVALID_FORMAT
}

data class ApiKeyValidationResult(
    val status: ApiKeyStatus,
    val isConfigured: Boolean,
    val userMessage: String,
    val maskedKey: String
)

class GeminiService {

    fun validateApiKeyPresence(): ApiKeyValidationResult {
        val result = GeminiConfigValidator.validateConfig()
        return ApiKeyValidationResult(
            status = when (result.status) {
                GeminiConfigStatus.CONFIGURED -> ApiKeyStatus.VALID_CONFIGURED
                GeminiConfigStatus.INVALID_FORMAT -> ApiKeyStatus.INVALID_FORMAT
                else -> ApiKeyStatus.MISSING_OR_PLACEHOLDER
            },
            isConfigured = !result.isFailSafeMode,
            userMessage = result.userMessage,
            maskedKey = result.maskedKey
        )
    }



    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    // Structured Prompt Builders
    fun buildThreatAnalysisPrompt(scenario: String, context: String): String {
        return """
            [AEGIS THREAT MODELING PROMPT]
            Scenario: $scenario
            Context / Target Infrastructure: $context
            
            Perform a zero-trust threat analysis and deliver:
            1. Identified Attack Vectors & Vulnerability Severity Rating (LOW, MEDIUM, HIGH, CRITICAL)
            2. Impact on System Integrity, Hardware Keystore, and Kernel Boundaries
            3. Actionable Cryptographic & Zero-Trust Mitigation Directives
        """.trimIndent()
    }

    fun buildBriefingPrompt(selinuxEnforced: Boolean, lockdownActive: Boolean, role: String): String {
        return """
            Generate an executive zero-trust security briefing for role '$role' in Acing IU: Genesis.
            Current System Posture:
            - SELinux Mode: ${if (selinuxEnforced) "Enforcing" else "Permissive"}
            - Zero-Trust Emergency Lockdown: ${if (lockdownActive) "Active" else "Disengaged"}
            - Hardware Keystore: StrongBox TEE Keymaster Verified
            
            Deliver a concise 3-bullet security evaluation highlighting status, threats, and zero-trust directives.
        """.trimIndent()
    }

    fun buildFirmwarePrompt(partitionName: String, sha256Hash: String, signatureStatus: String): String {
        return """
            Perform cryptographic and structural integrity evaluation on Android firmware image partition:
            Partition: $partitionName
            SHA-256 Digest: $sha256Hash
            AVB 2.0 Signature: $signatureStatus
            
            Provide:
            1. Cryptographic Signature & Verified Boot (AVB 2.0) status
            2. Potential risk vectors (e.g. bootkit injection, verity hash mismatch)
            3. Verification recommendation
        """.trimIndent()
    }

    fun buildTelemetryPrompt(deviceName: String, androidVersion: String, selinuxState: String, keystoreState: String): String {
        return """
            Conduct an AI Diagnostic sweep on target device telemetry:
            Device: $deviceName
            OS Version: $androidVersion
            SELinux Mode: $selinuxState
            Keystore Level: $keystoreState
            
            Provide:
            - System Health Assessment
            - Zero-Trust Hardware Attestation Status
            - Recommended Policy Optimizations
        """.trimIndent()
    }

    fun buildGovernancePrompt(roleLabel: String, roleLevel: String, apiKeyConfigured: Boolean): String {
        return """
            Audit governance posture and access controls for Acing IU: Genesis:
            Active Role: $roleLabel ($roleLevel)
            Secrets Configuration: ${if (apiKeyConfigured) "Live GEMINI_API_KEY Injected" else "Local Simulation / Key Unset"}
            
            Provide:
            1. RBAC Compliance Level
            2. Secrets & Build Profile Assessment
            3. Governance Recommendation
        """.trimIndent()
    }

    fun buildLogAnalysisPrompt(logSnippet: String, roleContext: String): String {
        return """
            As Aegis (Lead Systems Engineer & Security Architect for Acing IU: Genesis), conduct a high-precision security review of the following Android log snippet or system configuration:
            
            ```
            $logSnippet
            ```
            
            Provide:
            1. Identified Threats / Anomalies (CVE risk rating: LOW, MEDIUM, HIGH, CRITICAL)
            2. Impact on Zero-Trust Architecture & Device Integrity
            3. Actionable Technical Remediation Steps
        """.trimIndent()
    }

    suspend fun generateContent(
        prompt: String,
        systemPrompt: String,
        primaryModel: String = "gemini-2.5-flash",
        enableThinkingHigh: Boolean = false,
        fallbackGenerator: (String) -> String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext fallbackGenerator("Gemini API key not configured in AI Studio Secrets")
        }

        val modelsToTry = if (enableThinkingHigh) {
            listOf("gemini-3.1-pro-preview", "gemini-2.5-pro", "gemini-2.5-flash", "gemini-2.0-flash")
        } else {
            listOf(primaryModel, "gemini-2.5-flash", "gemini-2.0-flash", "gemini-1.5-flash", "gemini-2.5-pro")
        }.distinct()

        var lastError = ""

        for (model in modelsToTry) {
            var currentBackoff = 2000L
            val maxAttempts = 3

            for (attempt in 1..maxAttempts) {
                val (success, responseBody) = executeSingleCall(model, prompt, systemPrompt, enableThinkingHigh, apiKey)
                if (success && responseBody.isNotBlank()) {
                    return@withContext responseBody
                }
                lastError = responseBody

                // If error is non-retryable (401, 403 invalid key, or 404 model not found), break loop for this model
                if (responseBody.contains("403") || responseBody.contains("401") || responseBody.contains("404") || responseBody.contains("API_KEY_INVALID") || responseBody.lowercase().contains("invalid authentication credentials")) {
                    break
                }

                if (attempt < maxAttempts) {
                    delay(currentBackoff)
                    currentBackoff *= 2 // Exponential backoff
                }
            }
        }

        val cleanError = parseCleanErrorMessage(lastError)
        fallbackGenerator(cleanError)
    }

    suspend fun sendChatMessage(
        history: List<ChatMessage>,
        userMessage: String,
        useThinkingMode: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext """
                [AEGIS LOCAL CO-PILOT SIMULATION]
                Gemini API key is not configured in Secrets panel. To enable live Gemini AI intelligence, configure your GEMINI_API_KEY in AI Studio.
                
                Security Analysis for: "$userMessage"
                • Zero-Trust Policy: Active & Enforced
                • Recommendation: Verify hardware keymaster attestation and system partition hashes.
            """.trimIndent()
        }

        val modelsToTry = if (useThinkingMode) {
            listOf("gemini-3.1-pro-preview", "gemini-2.5-pro", "gemini-2.5-flash", "gemini-2.0-flash")
        } else {
            listOf("gemini-2.5-flash", "gemini-2.0-flash", "gemini-1.5-flash", "gemini-2.5-pro")
        }.distinct()

        val contentsArray = JSONArray()
        val recentHistory = history.takeLast(10)
        for (msg in recentHistory) {
            val contentObj = JSONObject()
            contentObj.put("role", if (msg.sender == "USER") "user" else "model")
            val partsArray = JSONArray()
            partsArray.put(JSONObject().put("text", msg.text))
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
        }

        val latestUserObj = JSONObject()
        latestUserObj.put("role", "user")
        val latestParts = JSONArray()
        latestParts.put(JSONObject().put("text", userMessage))
        latestUserObj.put("parts", latestParts)
        contentsArray.put(latestUserObj)

        val root = JSONObject()
        root.put("contents", contentsArray)

        val sysObj = JSONObject()
        val sysParts = JSONArray()
        sysParts.put(JSONObject().put("text", "You are Aegis, Principal Security Architect & Lead Systems Engineer for Acing IU: Genesis. You enforce the Genesis Agent AIs Code of Conduct, 14 specialized Agent AIs, least-privilege authority levels (Level 0-5), zero-trust policy governance, Android firmware research, and hardware attestation."))
        sysObj.put("parts", sysParts)
        root.put("systemInstruction", sysObj)

        val genConfig = JSONObject()
        genConfig.put("temperature", 0.3)
        root.put("generationConfig", genConfig)

        var lastError = ""

        for (model in modelsToTry) {
            var currentBackoff = 2000L
            for (attempt in 1..2) {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
                try {
                    val request = Request.Builder()
                        .url(url)
                        .post(root.toString().toRequestBody(jsonMediaType))
                        .build()

                    client.newCall(request).execute().use { response ->
                        val bodyStr = response.body?.string() ?: ""
                        if (response.isSuccessful) {
                            val resObj = JSONObject(bodyStr)
                            val candidates = resObj.optJSONArray("candidates")
                            if (candidates != null && candidates.length() > 0) {
                                val candidate = candidates.getJSONObject(0)
                                val content = candidate.optJSONObject("content")
                                val parts = content?.optJSONArray("parts")
                                if (parts != null && parts.length() > 0) {
                                    val text = parts.getJSONObject(0).optString("text", "")
                                    if (text.isNotBlank()) {
                                        return@withContext text
                                    }
                                }
                            }
                        } else {
                            lastError = bodyStr
                            // Break immediately on fatal authentication errors
                            if (lastError.contains("API_KEY_INVALID") || lastError.lowercase().contains("invalid authentication credentials")) {
                                break
                            }
                        }
                    }
                } catch (e: Exception) {
                    lastError = e.localizedMessage ?: "Network error"
                }
                delay(currentBackoff)
                currentBackoff *= 2
            }
        }

        val cleanError = parseCleanErrorMessage(lastError)
        """
            [AEGIS LOCAL CO-PILOT REPORT]
            Note: Live Gemini model endpoint is temporarily unavailable ($cleanError).
            
            Security Evaluation for: "$userMessage"
            • Posture: Acing IU: Genesis zero-trust security architecture is operational.
            • Recommendation: Check System Status, AVB 2.0 partition hashes, and RBAC policies. Re-try live chat once service demand normalizes.
        """.trimIndent()
    }

    private fun executeSingleCall(
        model: String,
        prompt: String,
        systemPrompt: String,
        enableThinkingHigh: Boolean,
        apiKey: String
    ): Pair<Boolean, String> {
        val root = JSONObject()
        val contentsArray = JSONArray()
        val userObj = JSONObject()
        userObj.put("role", "user")
        val parts = JSONArray()
        parts.put(JSONObject().put("text", prompt))
        userObj.put("parts", parts)
        contentsArray.put(userObj)
        root.put("contents", contentsArray)

        val sysObj = JSONObject()
        val sysParts = JSONArray()
        sysParts.put(JSONObject().put("text", systemPrompt))
        sysObj.put("parts", sysParts)
        root.put("systemInstruction", sysObj)

        val genConfig = JSONObject()
        genConfig.put("temperature", 0.2)
        root.put("generationConfig", genConfig)

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        return try {
            val request = Request.Builder()
                .url(url)
                .post(root.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    Pair(false, bodyStr)
                } else {
                    val resObj = JSONObject(bodyStr)
                    val candidates = resObj.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val candidate = candidates.getJSONObject(0)
                        val content = candidate.optJSONObject("content")
                        val resParts = content?.optJSONArray("parts")
                        if (resParts != null && resParts.length() > 0) {
                            val text = resParts.getJSONObject(0).optString("text", "")
                            if (text.isNotBlank()) Pair(true, text) else Pair(false, "Empty output")
                        } else Pair(false, "No parts")
                    } else Pair(false, "No candidates")
                }
            }
        } catch (e: Exception) {
            Pair(false, e.localizedMessage ?: "Network error")
        }
    }

    private fun parseCleanErrorMessage(rawError: String): String {
        return try {
            val lowerError = rawError.lowercase()
            
            if (lowerError.contains("invalid authentication credentials") || 
                lowerError.contains("api_key_invalid") || 
                lowerError.contains("api key not valid")) {
                "Invalid Gemini API Key. Please configure a valid GEMINI_API_KEY in the AI Studio Secrets panel."
            } else if (lowerError.contains("resource_exhausted") || lowerError.contains("quota") || lowerError.contains("429")) {
                "Gemini API quota/rate limit reached. Please check your AI Studio usage/quota or try again shortly."
            } else if (lowerError.contains("503") || lowerError.contains("unavailable") || lowerError.contains("high demand")) {
                "Gemini AI endpoint high demand (HTTP 503). Please try again later."
            } else if (rawError.contains("404")) {
                "Model endpoint not found."
            } else if (rawError.contains("error") && rawError.contains("message")) {
                val json = JSONObject(rawError)
                val errObj = json.optJSONObject("error")
                errObj?.optString("message") ?: "API Service Error"
            } else {
                "API service temporarily unavailable"
            }
        } catch (e: Exception) {
            "API service temporarily unavailable"
        }
    }

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
    }
}
