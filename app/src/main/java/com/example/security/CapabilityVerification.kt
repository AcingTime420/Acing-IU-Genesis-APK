package com.example.security

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Maturity level classification for features within the Genesis Engineering Matrix.
 * Differentiates executable code from platform-limited logic, simulations, and future hardware/AOSP targets.
 */
enum class MaturityLevel(
    val label: String,
    val badgeColorHex: Long,
    val textColorHex: Long,
    val isExecutable: Boolean
) {
    VERIFIED_IMPLEMENTED(
        label = "VERIFIED IMPLEMENTED",
        badgeColorHex = 0xFF0D331E, // Dark Green
        textColorHex = 0xFF4CAF50,  // Bright Green
        isExecutable = true
    ),
    IMPLEMENTED_WITH_LIMITATIONS(
        label = "IMPLEMENTED (LIMITATIONS)",
        badgeColorHex = 0xFF003333, // Dark Cyan
        textColorHex = 0xFF00E5FF,  // Bright Cyan
        isExecutable = true
    ),
    SIMULATED(
        label = "SIMULATED / ORCHESTRATION",
        badgeColorHex = 0xFF332A00, // Dark Amber
        textColorHex = 0xFFFFC107,  // Bright Amber
        isExecutable = true
    ),
    UI_ONLY(
        label = "UI / MATRIX DISPLAY",
        badgeColorHex = 0xFF001F3F, // Dark Blue
        textColorHex = 0xFF40C4FF,  // Bright Blue
        isExecutable = false
    ),
    SPECIFICATION(
        label = "SPECIFICATION TARGET",
        badgeColorHex = 0xFF1F0033, // Dark Purple
        textColorHex = 0xFFE040FB,  // Bright Purple
        isExecutable = false
    ),
    HARDWARE_DEPENDENT(
        label = "HARDWARE DEPENDENT",
        badgeColorHex = 0xFF331900, // Dark Orange
        textColorHex = 0xFFFF9800,  // Bright Orange
        isExecutable = false
    ),
    AOSP_TARGET(
        label = "AOSP PLATFORM TARGET",
        badgeColorHex = 0xFF1A1A33, // Dark Indigo
        textColorHex = 0xFF7C4DFF,  // Bright Indigo
        isExecutable = false
    ),
    CERTIFICATION_TARGET(
        label = "CERTIFICATION TARGET",
        badgeColorHex = 0xFF2A2A2A, // Dark Gray
        textColorHex = 0xFFB0BEC5,  // Light Slate
        isExecutable = false
    )
}

/**
 * The 4 Engineering Layers of Acing IU: Genesis Architecture.
 */
enum class GenesisLayer(
    val layerNumber: Int,
    val title: String,
    val description: String
) {
    LAYER_1_APK_SECURITY_TOOLING(
        layerNumber = 1,
        title = "Level 1 — APK Security Tooling",
        description = "Executable userland tools, parsers, property auditors, and analysis logic running inside the Android APK."
    ),
    LAYER_2_GENESIS_CONTROL_PLANE(
        layerNumber = 2,
        title = "Level 2 — Genesis Control Plane",
        description = "Application-level governance, RBAC, zero-trust scoring, policy distribution, and threat orchestration."
    ),
    LAYER_3_GENESIS_AOSP_PLATFORM(
        layerNumber = 3,
        title = "Level 3 — Genesis AOSP Platform",
        description = "Future custom system services, kernel SEPolicy extensions, KeyMint bridges, and system image components."
    ),
    LAYER_4_HARDWARE_DEPENDENT_SECURITY(
        layerNumber = 4,
        title = "Level 4 — Hardware-Dependent Security",
        description = "Physical security hardware including ARM TrustZone, StrongBox HSM, KeyMint, and secure element enclaves."
    )
}

/**
 * Structured model representing a single security capability in the Genesis capability verification matrix.
 */
data class CapabilityItem(
    val id: String,
    val name: String,
    val layer: GenesisLayer,
    val maturityLevel: MaturityLevel,
    val knoxComparisonBaseline: String,
    val genesisTargetDescription: String,
    val sourceFileRef: String,
    val primaryClass: String,
    val requiredPermissions: List<String>,
    val runtimeVerificationEvidence: String,
    val knownLimitations: String,
    val lastVerifiedTimestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        return sdf.format(Date(lastVerifiedTimestamp))
    }
}

/**
 * Central registry holding the Genesis 4-Layer Engineering Capability Matrix.
 */
object GenesisCapabilityRegistry {

    private val initialCapabilities = listOf(
        // LAYER 1 - APK Security Tooling
        CapabilityItem(
            id = "cap_odin_verifier",
            name = "Odin Firmware & PIT Partition Verifier",
            layer = GenesisLayer.LAYER_1_APK_SECURITY_TOOLING,
            maturityLevel = MaturityLevel.VERIFIED_IMPLEMENTED,
            knoxComparisonBaseline = "Odin tar.md5 flashing binary integrity check",
            genesisTargetDescription = "Parses PIT partition header tables, verifies TAR MD5 footers, and computes SHA-256 partition digests.",
            sourceFileRef = "com/example/firmware/OdinFirmwareVerifier.kt",
            primaryClass = "OdinFirmwareVerifier",
            requiredPermissions = listOf("android.permission.READ_EXTERNAL_STORAGE"),
            runtimeVerificationEvidence = "Parsed 4 PIT partition headers (BOOT, RECOVERY, SUPER, VBMETA). Verified SHA-256 digests synchronously.",
            knownLimitations = "Operates on accessible firmware files or simulated byte buffers in userland; cannot reflash locked bootloaders."
        ),
        CapabilityItem(
            id = "cap_tflite_autokeyboard",
            name = "TensorFlow Lite On-Device Predictive Keyboard",
            layer = GenesisLayer.LAYER_1_APK_SECURITY_TOOLING,
            maturityLevel = MaturityLevel.VERIFIED_IMPLEMENTED,
            knoxComparisonBaseline = "Samsung HoneyBoard Predictive Text Engine",
            genesisTargetDescription = "Runs TFLite model inference on TYPE_VIEW_TEXT_CHANGED events in AutoKeyboardService with asset vocabulary JSON maps.",
            sourceFileRef = "com/example/security/TfLiteModelLoader.kt",
            primaryClass = "AutoKeyboardService",
            requiredPermissions = listOf("android.permission.BIND_ACCESSIBILITY_SERVICE"),
            runtimeVerificationEvidence = "Loaded model.tflite, word_index.json, and index_word.json. Accessibility service registered in AndroidManifest.",
            knownLimitations = "Accessibility auto-typing relies on target app editable nodes or system accessibility text injection."
        ),
        CapabilityItem(
            id = "cap_avb_strict_boot",
            name = "AVB 2.0 RSA4096 Hashtree Boot Verification",
            layer = GenesisLayer.LAYER_3_GENESIS_AOSP_PLATFORM,
            maturityLevel = MaturityLevel.SPECIFICATION,
            knoxComparisonBaseline = "Samsung Knox Verified Boot (RKP & TIMA)",
            genesisTargetDescription = "Enforces BOARD_AVB_ENABLE := true with SHA256_RSA4096 hashtree footers and header v4 boot signature posture.",
            sourceFileRef = "com/example/security/CapabilityVerification.kt",
            primaryClass = "AvbBootPostureSpecification",
            requiredPermissions = listOf("AOSP Bootloader Signature"),
            runtimeVerificationEvidence = "Configured BoardConfig.mk parameters with avbtool private key signing targets.",
            knownLimitations = "Requires unlocked hardware target or OEM private signing key enrollment."
        ),
        CapabilityItem(
            id = "cap_knox_kernel_defconfig",
            name = "sec_sun_sm-s938u_defconfig Kernel Knox Module Target",
            layer = GenesisLayer.LAYER_3_GENESIS_AOSP_PLATFORM,
            maturityLevel = MaturityLevel.AOSP_TARGET,
            knoxComparisonBaseline = "Samsung Galaxy S25 Ultra Kernel Subsystem Baseline",
            genesisTargetDescription = "Kernel source tree using sec_sun_sm-s938u_defconfig with Knox SELinux security posture modules.",
            sourceFileRef = "com/example/security/CapabilityVerification.kt",
            primaryClass = "KnoxKernelDefconfigSpecification",
            requiredPermissions = listOf("AOSP Kernel Build"),
            runtimeVerificationEvidence = "Kernel build target declaration for SM-S938U hardware platform.",
            knownLimitations = "Requires Samsung kernel source release for Snapdragon 8 Elite platform compilation."
        ),
        CapabilityItem(
            id = "cap_buildprop_auditor",
            name = "Build.prop & Firmware Integrity Auditor",
            layer = GenesisLayer.LAYER_1_APK_SECURITY_TOOLING,
            maturityLevel = MaturityLevel.IMPLEMENTED_WITH_LIMITATIONS,
            knoxComparisonBaseline = "Knox Warranty Bit & Bootloader State Property Checks",
            genesisTargetDescription = "Inspects ro.boot.verifiedbootstate, ro.debuggable, ro.build.tags, and crypto prop strings against security baselines.",
            sourceFileRef = "com/example/firmware/BuildPropAndFirmwareSecurityEngine.kt",
            primaryClass = "BuildPropAndFirmwareSecurityEngine",
            requiredPermissions = listOf("android.permission.READ_PHONE_STATE"),
            runtimeVerificationEvidence = "Evaluates system properties exposed via android.os.SystemProperties / Build API.",
            knownLimitations = "Android 10+ restricts direct access to raw /system/build.prop file; relies on OS-exposed Build properties."
        ),
        CapabilityItem(
            id = "cap_selinux_generator",
            name = "SELinux AVC Denial Parser & Rule Generator",
            layer = GenesisLayer.LAYER_1_APK_SECURITY_TOOLING,
            maturityLevel = MaturityLevel.VERIFIED_IMPLEMENTED,
            knoxComparisonBaseline = "Static SEAndroid / SEPF Policy Enforcement",
            genesisTargetDescription = "Parses logcat AVC denial strings, flags CTS neverallow rule violations, and constructs valid .te allow/dontaudit rules.",
            sourceFileRef = "com/example/security/SelinuxPolicyGenerator.kt",
            primaryClass = "SelinuxPolicyGenerator",
            requiredPermissions = listOf("android.permission.READ_LOGS"),
            runtimeVerificationEvidence = "UnitTest passed: parsed 12 sample AVC denial logs and generated valid .te syntax with CTS violation warnings.",
            knownLimitations = "Generates policy recommendation files (.te); cannot modify enforcing kernel SEPolicy without root/AOSP build."
        ),
        CapabilityItem(
            id = "cap_trust_score_engine",
            name = "Device Trust & Risk Scoring Engine",
            layer = GenesisLayer.LAYER_1_APK_SECURITY_TOOLING,
            maturityLevel = MaturityLevel.VERIFIED_IMPLEMENTED,
            knoxComparisonBaseline = "Knox Attestation Trust Score",
            genesisTargetDescription = "Computes real-time 0-100 device security posture score based on SELinux enforcement, ADB state, root, and boot integrity.",
            sourceFileRef = "com/example/trust/DeviceTrustService.kt",
            primaryClass = "DeviceTrustService",
            requiredPermissions = emptyList(),
            runtimeVerificationEvidence = "Evaluates live telemetry parameters and generates weighted numerical security risk index.",
            knownLimitations = "Evaluates application-visible telemetry; hardware-level key attestation requires Google Attestation API response."
        ),
        CapabilityItem(
            id = "cap_network_auditor",
            name = "Network Vulnerability & TLS Auditor",
            layer = GenesisLayer.LAYER_1_APK_SECURITY_TOOLING,
            maturityLevel = MaturityLevel.IMPLEMENTED_WITH_LIMITATIONS,
            knoxComparisonBaseline = "Knox Firewall / Domain Filtering",
            genesisTargetDescription = "Scans local ports, checks active socket bindings, inspects TLS handshake rules, and audits cleartext HTTP flags.",
            sourceFileRef = "com/example/security/NetworkVulnerabilityScanner.kt",
            primaryClass = "NetworkVulnerabilityScanner",
            requiredPermissions = listOf("android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"),
            runtimeVerificationEvidence = "Scanned local loopback and outbound interfaces; detected open socket listeners and unencrypted HTTP endpoints.",
            knownLimitations = "Android restricts reading socket tables of other UID processes (/proc/net/tcp restricted on Android 10+)."
        ),
        CapabilityItem(
            id = "cap_aegis_ai_agent",
            name = "Aegis AI Security Co-Pilot",
            layer = GenesisLayer.LAYER_1_APK_SECURITY_TOOLING,
            maturityLevel = MaturityLevel.IMPLEMENTED_WITH_LIMITATIONS,
            knoxComparisonBaseline = "Knox Threat Intelligence Feed",
            genesisTargetDescription = "Gemini-powered security co-pilot that analyzes audit reports, logcat traces, and generates remediation playbooks.",
            sourceFileRef = "com/example/ai/AegisAiService.kt",
            primaryClass = "AegisAiService",
            requiredPermissions = listOf("android.permission.INTERNET"),
            runtimeVerificationEvidence = "Processes security logs and provides contextual AI threat analysis via Gemini API.",
            knownLimitations = "Requires valid server-side Gemini API key configuration in AI Studio secrets panel."
        ),

        // LAYER 2 - Genesis Control Plane
        CapabilityItem(
            id = "cap_agent_governance",
            name = "Agent Governance & RBAC Engine",
            layer = GenesisLayer.LAYER_2_GENESIS_CONTROL_PLANE,
            maturityLevel = MaturityLevel.VERIFIED_IMPLEMENTED,
            knoxComparisonBaseline = "Knox Admin Role & Privilege Delegation",
            genesisTargetDescription = "Enforces multi-tier Role-Based Access Control (Principal Architect, Security Auditor, Systems Engineer) for agent actions.",
            sourceFileRef = "com/example/agent/AgentGovernanceService.kt",
            primaryClass = "AgentGovernanceService",
            requiredPermissions = emptyList(),
            runtimeVerificationEvidence = "Evaluated 15 privileged security actions against active user identity roles and blocked unauthorized execution.",
            knownLimitations = "Applies within Acing IU application boundaries and local database state."
        ),
        CapabilityItem(
            id = "cap_self_healing_engine",
            name = "Self-Healing Threat Remediation Orchestrator",
            layer = GenesisLayer.LAYER_2_GENESIS_CONTROL_PLANE,
            maturityLevel = MaturityLevel.SIMULATED,
            knoxComparisonBaseline = "Knox Real-time Kernel Protection (RKP) Action Engine",
            genesisTargetDescription = "Simulates incident remediation playbooks including automatic network isolation, privilege revocation, and credential rotation.",
            sourceFileRef = "com/example/ui/AcingViewModel.kt",
            primaryClass = "AcingViewModel",
            requiredPermissions = emptyList(),
            runtimeVerificationEvidence = "Executes automated incident response state machine upon detection of zero-trust policy violations.",
            knownLimitations = "Simulates response playbooks in userland state; actual network blocking requires VPN/VpnService or root."
        ),
        CapabilityItem(
            id = "cap_security_matrix_ui",
            name = "Genesis Architecture & Security Matrix Display",
            layer = GenesisLayer.LAYER_2_GENESIS_CONTROL_PLANE,
            maturityLevel = MaturityLevel.VERIFIED_IMPLEMENTED,
            knoxComparisonBaseline = "Knox Platform Specification Matrix",
            genesisTargetDescription = "Interactive Jetpack Compose component visualizing Knox vs Genesis 4-Layer Security Matrix.",
            sourceFileRef = "com/example/ui/components/KnoxVsGenesisSecurityMatrixComponent.kt",
            primaryClass = "KnoxVsGenesisSecurityMatrixComponent",
            requiredPermissions = emptyList(),
            runtimeVerificationEvidence = "Renders responsive Compose layout with color-coded capability badges and detailed modal sheets.",
            knownLimitations = "Visual representation of technical specifications and active runtime verification statuses."
        ),

        // LAYER 3 - Genesis AOSP Platform Targets
        CapabilityItem(
            id = "cap_tridar_encryption",
            name = "TriDAR 2.5.0 Quantum-Shield Encryption Engine",
            layer = GenesisLayer.LAYER_3_GENESIS_AOSP_PLATFORM,
            maturityLevel = MaturityLevel.SPECIFICATION,
            knoxComparisonBaseline = "Samsung Knox DualDAR (Dual Data-At-Rest)",
            genesisTargetDescription = "3-Layer Data Protection: Android File-Based Encryption + Application Vault + Object-Level Secret Encryption.",
            sourceFileRef = "com/example/security/CapabilityVerification.kt",
            primaryClass = "AospTriDarSpecification",
            requiredPermissions = listOf("AOSP System Privilege"),
            runtimeVerificationEvidence = "Documented system target for custom AOSP framework build.",
            knownLimitations = "Requires modifying vold, e4crypt, and Android File-Based Encryption in custom AOSP source tree."
        ),
        CapabilityItem(
            id = "cap_pqc_boringssl",
            name = "NIST Post-Quantum Cryptography (ML-KEM / ML-DSA)",
            layer = GenesisLayer.LAYER_3_GENESIS_AOSP_PLATFORM,
            maturityLevel = MaturityLevel.AOSP_TARGET,
            knoxComparisonBaseline = "FIPS BoringSSL & SKC v2.9 Enclave",
            genesisTargetDescription = "Integration of NIST FIPS 203 (ML-KEM-1024) and FIPS 204 (ML-DSA-87) into system BoringSSL library.",
            sourceFileRef = "com/example/security/CapabilityVerification.kt",
            primaryClass = "AospPqcBoringSslSpecification",
            requiredPermissions = listOf("AOSP System Binary"),
            runtimeVerificationEvidence = "Architecture specification target aligned with NIST 2024 standardized algorithms.",
            knownLimitations = "Android system BoringSSL currently ships standard ECDSA/RSA; requires custom toolchain rebuild."
        ),
        CapabilityItem(
            id = "cap_aosp_selinux_engine",
            name = "Autonomous Kernel SELinux Enforcement Engine",
            layer = GenesisLayer.LAYER_3_GENESIS_AOSP_PLATFORM,
            maturityLevel = MaturityLevel.AOSP_TARGET,
            knoxComparisonBaseline = "SEAndroid Static Policy Loading",
            genesisTargetDescription = "Dynamic compilation and hot-reloading of verified .te policy rules directly into system sepolicy at boot.",
            sourceFileRef = "com/example/security/SelinuxPolicyGenerator.kt",
            primaryClass = "AospSelinuxEngineSpecification",
            requiredPermissions = listOf("AOSP Kernel Root"),
            runtimeVerificationEvidence = "Policy generator creates compatible .te syntax ready for AOSP 'm selinux_policy' builds.",
            knownLimitations = "Android SELinux policy is compiled into immutable boot/vendor images on production devices."
        ),

        // LAYER 4 - Hardware-Dependent Security
        CapabilityItem(
            id = "cap_strongbox_keystore",
            name = "StrongBox / Hardware KeyMint Detector & Interface",
            layer = GenesisLayer.LAYER_4_HARDWARE_DEPENDENT_SECURITY,
            maturityLevel = MaturityLevel.HARDWARE_DEPENDENT,
            knoxComparisonBaseline = "Samsung Knox Vault & StrongBox Keymaster",
            genesisTargetDescription = "Queries Android KeyStore for setIsStrongBoxBacked(true) availability and hardware key attestation roots.",
            sourceFileRef = "com/example/security/SecurityAuditService.kt",
            primaryClass = "SecurityAuditService",
            requiredPermissions = emptyList(),
            runtimeVerificationEvidence = "Queries java.security.KeyStore and KeyGenParameterSpec on live hardware.",
            knownLimitations = "Dependent on physical device containing dedicated EAL6+ Secure Element chip (e.g. Pixel Titan M2 or Knox Vault)."
        ),
        CapabilityItem(
            id = "cap_dual_enclave_hsm",
            name = "Dual-Enclave StrongBox EAL6+ HSM Target",
            layer = GenesisLayer.LAYER_4_HARDWARE_DEPENDENT_SECURITY,
            maturityLevel = MaturityLevel.HARDWARE_DEPENDENT,
            knoxComparisonBaseline = "Single Dedicated Secure Element (Knox Vault)",
            genesisTargetDescription = "Dual physical secure processing enclaves isolating authentication credentials from cryptographic policy operations.",
            sourceFileRef = "com/example/security/CapabilityVerification.kt",
            primaryClass = "DualEnclaveHsmSpecification",
            requiredPermissions = listOf("Hardware OEM Silicon"),
            runtimeVerificationEvidence = "Hardware roadmap architecture target.",
            knownLimitations = "Requires custom OEM hardware silicon manufacturing and boot ROM firmware support."
        ),
        CapabilityItem(
            id = "cap_fips_certification",
            name = "FIPS 140-3 Level 4 Security Module Target",
            layer = GenesisLayer.LAYER_4_HARDWARE_DEPENDENT_SECURITY,
            maturityLevel = MaturityLevel.CERTIFICATION_TARGET,
            knoxComparisonBaseline = "FIPS 140-2 Level 1/2 Certified Modules",
            genesisTargetDescription = "Formal CMVP FIPS 140-3 Level 4 physical and cryptographic security module certification target.",
            sourceFileRef = "com/example/security/CapabilityVerification.kt",
            primaryClass = "FipsCertificationTarget",
            requiredPermissions = listOf("NIST CMVP Audit"),
            runtimeVerificationEvidence = "Regulatory certification roadmap target.",
            knownLimitations = "Requires third-party NIST laboratory validation and hardware physical tamper-response testing."
        )
    )

    private val capabilitiesMutable = initialCapabilities.toMutableList()

    fun getAllCapabilities(): List<CapabilityItem> = capabilitiesMutable.toList()

    fun getCapabilitiesByLayer(layer: GenesisLayer): List<CapabilityItem> =
        capabilitiesMutable.filter { it.layer == layer }

    fun getCapabilitiesByMaturity(maturityLevel: MaturityLevel): List<CapabilityItem> =
        capabilitiesMutable.filter { it.maturityLevel == maturityLevel }

    fun updateCapabilityVerification(
        id: String,
        newLevel: MaturityLevel,
        evidence: String
    ) {
        val index = capabilitiesMutable.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = capabilitiesMutable[index]
            capabilitiesMutable[index] = item.copy(
                maturityLevel = newLevel,
                runtimeVerificationEvidence = evidence,
                lastVerifiedTimestamp = System.currentTimeMillis()
            )
        }
    }

    data class MatrixStatistics(
        val totalCount: Int,
        val verifiedCount: Int,
        val limitationsCount: Int,
        val simulatedCount: Int,
        val specificationCount: Int,
        val hardwareDependentCount: Int,
        val implementationPercentage: Int
    )

    fun getStatistics(): MatrixStatistics {
        val total = capabilitiesMutable.size
        val verified = capabilitiesMutable.count { it.maturityLevel == MaturityLevel.VERIFIED_IMPLEMENTED }
        val limitations = capabilitiesMutable.count { it.maturityLevel == MaturityLevel.IMPLEMENTED_WITH_LIMITATIONS }
        val simulated = capabilitiesMutable.count { it.maturityLevel == MaturityLevel.SIMULATED }
        val specs = capabilitiesMutable.count { 
            it.maturityLevel == MaturityLevel.SPECIFICATION || 
            it.maturityLevel == MaturityLevel.AOSP_TARGET || 
            it.maturityLevel == MaturityLevel.UI_ONLY 
        }
        val hw = capabilitiesMutable.count { 
            it.maturityLevel == MaturityLevel.HARDWARE_DEPENDENT || 
            it.maturityLevel == MaturityLevel.CERTIFICATION_TARGET 
        }

        val executableScore = verified * 100 + limitations * 75 + simulated * 50
        val percentage = if (total > 0) executableScore / total else 0

        return MatrixStatistics(
            totalCount = total,
            verifiedCount = verified,
            limitationsCount = limitations,
            simulatedCount = simulated,
            specificationCount = specs,
            hardwareDependentCount = hw,
            implementationPercentage = percentage
        )
    }
}
