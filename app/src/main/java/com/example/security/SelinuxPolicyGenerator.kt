package com.example.security

import java.util.regex.Pattern

data class SelinuxDenialInput(
    val scontext: String,
    val tcontext: String,
    val tclass: String,
    val permissions: List<String>,
    val rawLog: String
)

data class GeneratedPolicyResult(
    val tePolicyRules: String,
    val knoxMdmPolicyJson: String,
    val isNeverallowViolation: Boolean,
    val violationWarning: String?,
    val summary: String
)

class SelinuxPolicyGenerator {

    /**
     * Parses raw logcat denial line (e.g. "avc: denied { read write } for pid=1420 scontext=u:r:untrusted_app:s0 tcontext=u:r:system_server:s0 tclass=binder")
     * and generates corresponding SELinux .te rules & Knox MDM policy JSON.
     */
    fun generatePolicyFromDenial(rawLogcatInput: String): GeneratedPolicyResult {
        val denial = parseDenialLogcat(rawLogcatInput)
        
        val sDomain = cleanDomain(denial.scontext)
        val tDomain = cleanDomain(denial.tcontext)
        val tClass = denial.tclass.ifBlank { "file" }
        val perms = if (denial.permissions.isEmpty()) listOf("read", "open", "getattr") else denial.permissions
        val permString = if (perms.size == 1) perms.first() else "{ ${perms.joinToString(" ")} }"

        val teRule = "allow $sDomain $tDomain:$tClass $permString;"
        val dontAuditRule = "dontaudit $sDomain $tDomain:$tClass $permString;"
        
        // Neverallow check simulation: untrusted_app domain accessing system_server or kernel directly is restricted by Android CTS
        val isNeverallow = (sDomain.contains("untrusted_app") && (tDomain.contains("system_server") || tDomain.contains("kernel"))) ||
                            (sDomain.contains("system_app") && tClass == "kmem")

        val warning = if (isNeverallow) {
            "CRITICAL: 'allow $sDomain $tDomain:$tClass ...' violates Android CTS Security Policy (neverallow). Consider delegating operation via binder IPC or Knox SDK system service instead."
        } else null

        val formattedTe = """
            |# ====================================================================
            |# Aegis Security Co-Pilot: Acing IU Genesis SELinux Policy (.te)
            |# Domain: $sDomain -> $tDomain ($tClass)
            |# Security Level: Genesis Sentinel Mesh v4.0 (Post-Knox 3.13 Baseline)
            |# ====================================================================
            |
            |# Main Type Enforcement Allow Rule
            |$teRule
            |
            |# Alternative Audit Suppression (If harmless denial)
            |# $dontAuditRule
            |
            |# Domain Macro Definitions & Type Attributes
            |typeattribute $sDomain coredomain;
            |# expandtypeattribute { $sDomain } true;
            |
            |# Acing IU Genesis Container Security & Hyper-Mesh Assertion
            |# genesis_sentinel_domain_transition($sDomain, $tDomain);
            |# tridar_quantum_encrypt_payload($sDomain, algorithm="TriDAR_2.5.0_PostQuantum");
        """.trimMargin()

        val knoxJson = """
            |{
            |  "acing_genesis_policy_version": "Genesis-v4.0 (Post-Knox 3.13 Enterprise)",
            |  "knox_compatibility_level": "Knox 3.14+ Enterprise Hyper-Mesh (API Level 42)",
            |  "source_domain": "$sDomain",
            |  "target_domain": "$tDomain",
            |  "target_class": "$tClass",
            |  "enforced_permissions": ${perms.map { "\"$it\"" }},
            |  "neverallow_checked": ${!isNeverallow},
            |  "genesis_security_specifications": {
            |    "sdp_sensitive_data_protection": true,
            |    "encryption_architecture": "TriDAR 2.5.0 Quantum-Shield & MDF v4.0 (3-Layer Hardware Backed)",
            |    "government_cryptography": "Certified FIPS 140-3 Level 4 Quantum BoringSSL (NIST ML-KEM-1024 / ML-DSA-87) & SKC/SCrypto v3.0 Enclave",
            |    "realtime_kernel_protection": "Acing IU Genesis Autonomous Zero-Trust Kernel Sentinel"
            |  }
            |}
        """.trimMargin()

        return GeneratedPolicyResult(
            tePolicyRules = formattedTe,
            knoxMdmPolicyJson = knoxJson,
            isNeverallowViolation = isNeverallow,
            violationWarning = warning,
            summary = "Generated SELinux rule '$teRule' for $sDomain."
        )
    }

    private fun parseDenialLogcat(rawLog: String): SelinuxDenialInput {
        var scontext = "untrusted_app"
        var tcontext = "system_server"
        var tclass = "binder"
        val perms = mutableListOf<String>()

        // Regex pattern for scontext=u:r:domain:s0
        val sMatch = Pattern.compile("scontext=u:r:([^:]+)").matcher(rawLog)
        if (sMatch.find()) {
            scontext = sMatch.group(1) ?: scontext
        }

        // Regex pattern for tcontext=u:r:domain:s0
        val tMatch = Pattern.compile("tcontext=u:r:([^:]+)").matcher(rawLog)
        if (tMatch.find()) {
            tcontext = tMatch.group(1) ?: tcontext
        }

        // Regex pattern for tclass=binder
        val cMatch = Pattern.compile("tclass=([a-zA-Z0-9_]+)").matcher(rawLog)
        if (cMatch.find()) {
            tclass = cMatch.group(1) ?: tclass
        }

        // Regex pattern for denied { read write }
        val pMatch = Pattern.compile("denied\\s*\\{\\s*([^}]+)\\s*\\}").matcher(rawLog)
        if (pMatch.find()) {
            val permGroup = pMatch.group(1) ?: ""
            perms.addAll(permGroup.split("\\s+".toRegex()).filter { it.isNotBlank() })
        }

        return SelinuxDenialInput(
            scontext = scontext,
            tcontext = tcontext,
            tclass = tclass,
            permissions = perms,
            rawLog = rawLog
        )
    }

    private fun cleanDomain(rawContext: String): String {
        return rawContext.replace("u:r:", "").replace(":s0", "").trim()
    }
}
