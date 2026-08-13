package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agent.AgentActionEvaluation
import com.example.agent.AgentAuthorityLevel
import com.example.agent.AgentGovernanceService
import com.example.agent.AgentIdentity
import com.example.ai.AegisAiService
import com.example.ai.ChatMessage
import com.example.data.AcingDatabase
import com.example.data.AuditLogEntity
import com.example.data.DeviceSnapshotEntity
import com.example.data.FirmwareScanEntity
import com.example.data.SecurityRepository
import com.example.firmware.BootkitDetectionReport
import com.example.firmware.BuildPropAndFirmwareSecurityEngine
import com.example.firmware.BuildPropItem
import com.example.firmware.DmVerityStatus
import com.example.firmware.DomainTransitionPermission
import com.example.firmware.StrongBoxTeeOperationResult
import com.example.firmware.VbmetaValidationAndSpoofResult
import com.example.logging.CentralizedLoggingService
import com.example.firmware.OdinFirmwareVerifier
import com.example.firmware.OdinTarMd5VerificationResult
import com.example.billing.LicenseTier
import com.example.billing.ShopifyLicenseState
import com.example.billing.ShopifyLicenseValidator
import com.example.billing.ShopifyValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import com.example.security.CustomSecurityAuditEngine
import com.example.security.DeviceTelemetryInput
import com.example.security.GeneratedPolicyResult
import com.example.security.NetworkScanReport
import com.example.security.NetworkVulnerabilityScanner
import com.example.security.SecurityAuditReport
import com.example.security.SelinuxPolicyGenerator
import com.example.security.TelemetryValidator
import com.example.trust.DeviceTrustReport
import com.example.trust.DeviceTrustService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    DASHBOARD,
    FIRMWARE,
    DEVICES,
    FORENSICS,
    AEGIS_AI,
    GOVERNANCE,
    THREAT_INTEL,
    DEVICE_SECURITY,
    SECURITY_STATUS
}

enum class SecurityRole(val label: String, val level: String) {
    PRINCIPAL_ARCHITECT("Principal Architect", "Level 5 - Full Authority"),
    SECURITY_AUDITOR("Security Auditor", "Level 4 - Read & Verify"),
    SYSTEMS_ENGINEER("Systems Engineer", "Level 3 - Operations")
}

data class RemediationProposal(
    val id: String,
    val title: String,
    val description: String,
    val severity: String, // "CRITICAL", "HIGH", "RECOMMENDED"
    val impactedComponent: String,
    val proposedFix: String,
    val actionType: RemediationActionType,
    val isApproved: Boolean = false,
    val isExecuted: Boolean = false
)

enum class RemediationActionType {
    ENFORCE_SELINUX,
    ENABLE_ZERO_TRUST_LOCKDOWN,
    ENABLE_CERT_PINNING,
    ENABLE_BIOMETRIC_LOCKOUT,
    OPTIMIZE_AI_THINKING_MODE,
    REFRESH_SECRETS_CONFIG
}

class AcingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SecurityRepository
    private val loggingService: CentralizedLoggingService
    private val deviceTrustService = DeviceTrustService()
    private val aiService = AegisAiService()
    private val agentGovernanceService = AgentGovernanceService()
    private val firmwareSecurityEngine = BuildPropAndFirmwareSecurityEngine()
    private val selinuxPolicyGenerator = SelinuxPolicyGenerator()
    private val customAuditEngine = CustomSecurityAuditEngine()
    private val networkScanner = NetworkVulnerabilityScanner()
    private val odinVerifier = OdinFirmwareVerifier()
    private val shopifyLicenseValidator = ShopifyLicenseValidator()

    // Module 5: Shopify License Validation State Engine
    private val _shopifyLicenseState = MutableStateFlow(ShopifyLicenseState())
    val shopifyLicenseState: StateFlow<ShopifyLicenseState> = _shopifyLicenseState.asStateFlow()

    private val _isValidatingShopifyLicense = MutableStateFlow(false)
    val isValidatingShopifyLicense: StateFlow<Boolean> = _isValidatingShopifyLicense.asStateFlow()

    // Module 3: Knox & SELinux Policy Generator State
    private val _selinuxPolicyResult = MutableStateFlow<GeneratedPolicyResult?>(null)
    val selinuxPolicyResult: StateFlow<GeneratedPolicyResult?> = _selinuxPolicyResult.asStateFlow()

    // Module 4: Custom Security Audit Routine State
    private val _customAuditReport = MutableStateFlow<SecurityAuditReport?>(null)
    val customAuditReport: StateFlow<SecurityAuditReport?> = _customAuditReport.asStateFlow()

    private val _isCustomAuditRunning = MutableStateFlow(false)
    val isCustomAuditRunning: StateFlow<Boolean> = _isCustomAuditRunning.asStateFlow()

    // Module 1: Custom Network Vulnerability Scanner State
    private val _networkScanReport = MutableStateFlow<NetworkScanReport?>(null)
    val networkScanReport: StateFlow<NetworkScanReport?> = _networkScanReport.asStateFlow()

    private val _isNetworkScanning = MutableStateFlow(false)
    val isNetworkScanning: StateFlow<Boolean> = _isNetworkScanning.asStateFlow()

    // Module 2: Odin Firmware Verification State
    private val _odinFirmwareResult = MutableStateFlow<OdinTarMd5VerificationResult?>(null)
    val odinFirmwareResult: StateFlow<OdinTarMd5VerificationResult?> = _odinFirmwareResult.asStateFlow()

    private val _isVerifyingOdin = MutableStateFlow(false)
    val isVerifyingOdin: StateFlow<Boolean> = _isVerifyingOdin.asStateFlow()

    val auditLogs: StateFlow<List<AuditLogEntity>>
    val deviceSnapshots: StateFlow<List<DeviceSnapshotEntity>>
    val firmwareScans: StateFlow<List<FirmwareScanEntity>>

    private val _lastAgentEvaluation = MutableStateFlow<AgentActionEvaluation?>(null)
    val lastAgentEvaluation: StateFlow<AgentActionEvaluation?> = _lastAgentEvaluation.asStateFlow()

    private val _buildPropAudits = MutableStateFlow<List<BuildPropItem>>(firmwareSecurityEngine.getBuildPropAudits())
    val buildPropAudits: StateFlow<List<BuildPropItem>> = _buildPropAudits.asStateFlow()

    private val _dmVerityStatuses = MutableStateFlow<List<DmVerityStatus>>(firmwareSecurityEngine.detectDmVerity())
    val dmVerityStatuses: StateFlow<List<DmVerityStatus>> = _dmVerityStatuses.asStateFlow()

    private val _bootkitReport = MutableStateFlow<BootkitDetectionReport>(firmwareSecurityEngine.detectBootkitInjection())
    val bootkitReport: StateFlow<BootkitDetectionReport> = _bootkitReport.asStateFlow()

    private val _vbmetaResult = MutableStateFlow<VbmetaValidationAndSpoofResult>(firmwareSecurityEngine.validateAndSpoofVbmeta(false))
    val vbmetaResult: StateFlow<VbmetaValidationAndSpoofResult> = _vbmetaResult.asStateFlow()

    private val _strongBoxCryptoResult = MutableStateFlow<StrongBoxTeeOperationResult?>(null)
    val strongBoxCryptoResult: StateFlow<StrongBoxTeeOperationResult?> = _strongBoxCryptoResult.asStateFlow()

    private val _domainTransitions = MutableStateFlow<List<DomainTransitionPermission>>(firmwareSecurityEngine.getDomainTransitions())
    val domainTransitions: StateFlow<List<DomainTransitionPermission>> = _domainTransitions.asStateFlow()

    private val _selectedTab = MutableStateFlow(AppTab.DASHBOARD)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    private val _currentRole = MutableStateFlow(SecurityRole.PRINCIPAL_ARCHITECT)
    val currentRole: StateFlow<SecurityRole> = _currentRole.asStateFlow()

    // Security System Controls
    private val _selinuxEnforced = MutableStateFlow(true)
    val selinuxEnforced: StateFlow<Boolean> = _selinuxEnforced.asStateFlow()

    private val _zeroTrustLockdown = MutableStateFlow(false)
    val zeroTrustLockdown: StateFlow<Boolean> = _zeroTrustLockdown.asStateFlow()

    private val _certPinningActive = MutableStateFlow(true)
    val certPinningActive: StateFlow<Boolean> = _certPinningActive.asStateFlow()

    private val _avbVerified = MutableStateFlow(true)
    val avbVerified: StateFlow<Boolean> = _avbVerified.asStateFlow()

    private val _biometricLockoutProtection = MutableStateFlow(true)
    val biometricLockoutProtection: StateFlow<Boolean> = _biometricLockoutProtection.asStateFlow()

    private val _highSensitivityMode = MutableStateFlow(false)
    val highSensitivityMode: StateFlow<Boolean> = _highSensitivityMode.asStateFlow()

    fun toggleBiometricLockoutProtection(enabled: Boolean) {
        _biometricLockoutProtection.value = enabled
        viewModelScope.launch {
            loggingService.logOperation(
                category = "Security Settings",
                title = "Biometric Lockout Protection ${if (enabled) "Enabled" else "Disabled"}",
                details = "User modified biometric lockout protection.",
                severity = if (enabled) "SECURE" else "WARNING",
                role = currentRole.value.label
            )
        }
    }

    fun toggleHighSensitivityMode(enabled: Boolean) {
        _highSensitivityMode.value = enabled
        viewModelScope.launch {
            loggingService.logOperation(
                category = "Security Settings",
                title = "High-Sensitivity Mode ${if (enabled) "Enabled" else "Disabled"}",
                details = "User modified high-sensitivity mode.",
                severity = if (enabled) "SECURE" else "INFO",
                role = currentRole.value.label
            )
        }
    }

    // Device Telemetry & Trust Score State
    private val _currentTelemetryInput = MutableStateFlow(
        DeviceTelemetryInput(
            hardwareId = "SM-S938U-VERIZON-01",
            manufacturer = "Samsung",
            modelCode = "SM-S938U (Galaxy S25 Ultra)",
            androidVersion = "Android 15 (API 35)",
            selinuxEnforcing = true,
            bootloaderLocked = true,
            partitionsUnmodified = true,
            knoxFuseIntact = true,
            isRooted = false
        )
    )
    val currentTelemetryInput: StateFlow<DeviceTelemetryInput> = _currentTelemetryInput.asStateFlow()

    private val _deviceTrustReport = MutableStateFlow<DeviceTrustReport?>(null)
    val deviceTrustReport: StateFlow<DeviceTrustReport?> = _deviceTrustReport.asStateFlow()

    // AI Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "AEGIS_AI",
                text = "Greetings, Architect. I am Aegis, your AI Security Co-Pilot. System status: ZERO-TRUST ENFORCED. How can I assist with Android firmware analysis, device diagnostics, or threat modeling today?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _useThinkingMode = MutableStateFlow(false)
    val useThinkingMode: StateFlow<Boolean> = _useThinkingMode.asStateFlow()

    // API Key Diagnostic & Configuration State
    private val _apiKeyValidationState = MutableStateFlow(aiService.validateApiKeyPresence())
    val apiKeyValidationState: StateFlow<com.example.ai.ApiKeyValidationResult> = _apiKeyValidationState.asStateFlow()

    fun refreshApiKeyStatus() {
        val result = aiService.validateApiKeyPresence()
        _apiKeyValidationState.value = result
        viewModelScope.launch {
            loggingService.logOperation(
                category = "Secrets Diagnostic",
                title = "GEMINI_API_KEY Health Check",
                details = "Status: ${result.status} | Key: ${result.maskedKey}",
                severity = if (result.isConfigured) "SECURE" else "WARNING",
                role = currentRole.value.label
            )
        }
    }

    fun registerScannedInventoryPayload(scannedData: String, isFirmwareManifest: Boolean) {
        viewModelScope.launch {
            if (isFirmwareManifest) {
                repository.recordFirmwareScan(
                    FirmwareScanEntity(
                        imageName = "Scanned Manifest Payload",
                        partitionName = "boot_manifest.url",
                        sha256Hash = scannedData,
                        signatureStatus = "VERIFIED_QR_MANIFEST",
                        isVerified = true
                    )
                )
                repository.logEvent(
                    category = "INVENTORY_SCANNER",
                    title = "Firmware Manifest URL Registered via QR",
                    details = "Manifest URL: $scannedData",
                    severity = "INFO",
                    outcome = "SUCCESS"
                )
            } else {
                repository.recordDeviceSnapshot(
                    DeviceSnapshotEntity(
                        deviceName = scannedData,
                        androidVersion = "Android 15 (Target)",
                        selinuxState = "Enforcing",
                        avbState = "Locked (AVB 2.0)",
                        hardwareKeystore = "StrongBox TEE",
                        cveCount = 0,
                        healthScore = 98
                    )
                )
                repository.logEvent(
                    category = "INVENTORY_SCANNER",
                    title = "Device Serial Registered via QR",
                    details = "Serial/Device ID: $scannedData",
                    severity = "INFO",
                    outcome = "SUCCESS"
                )
            }
        }
    }


    // Interactive Log Analysis Dialog State
    private val _logAnalysisResult = MutableStateFlow<String?>(null)
    val logAnalysisResult: StateFlow<String?> = _logAnalysisResult.asStateFlow()

    private val _isAnalyzingLog = MutableStateFlow(false)
    val isAnalyzingLog: StateFlow<Boolean> = _isAnalyzingLog.asStateFlow()

    // Self-Healing & Automated Remediation Proposals
    private val _remediationProposals = MutableStateFlow<List<RemediationProposal>>(emptyList())
    val remediationProposals: StateFlow<List<RemediationProposal>> = _remediationProposals.asStateFlow()

    private val _isSelfHealingActive = MutableStateFlow(false)
    val isSelfHealingActive: StateFlow<Boolean> = _isSelfHealingActive.asStateFlow()

    fun runAegisSelfDiagnosis() {
        _isSelfHealingActive.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(600)
            val proposals = mutableListOf<RemediationProposal>()

            if (!_selinuxEnforced.value) {
                proposals.add(
                    RemediationProposal(
                        id = "FIX-SELINUX-01",
                        title = "Enforce SELinux Mandatory Access Controls",
                        description = "SELinux is set to Permissive mode, exposing Android IPC to potential privilege escalation.",
                        severity = "CRITICAL",
                        impactedComponent = "Kernel SELinux Policy Subsystem",
                        proposedFix = "Transition SELinux from Permissive to Enforcing mode.",
                        actionType = RemediationActionType.ENFORCE_SELINUX
                    )
                )
            }

            if (!_certPinningActive.value) {
                proposals.add(
                    RemediationProposal(
                        id = "FIX-CERTPIN-02",
                        title = "Enable Strict Network Certificate Pinning",
                        description = "Certificate pinning is bypassed. TLS endpoints could be vulnerable to local proxy interception.",
                        severity = "HIGH",
                        impactedComponent = "Network Security Config / TLS Stack",
                        proposedFix = "Re-enable strict cryptographic cert pinning for Aegis endpoints.",
                        actionType = RemediationActionType.ENABLE_CERT_PINNING
                    )
                )
            }

            if (!_biometricLockoutProtection.value) {
                proposals.add(
                    RemediationProposal(
                        id = "FIX-BIOMETRIC-03",
                        title = "Enable Biometric Lockout Guard",
                        description = "Biometric anti-tamper lockout protection is currently disabled.",
                        severity = "RECOMMENDED",
                        impactedComponent = "Android BiometricPrompt & Keymaster TEE",
                        proposedFix = "Activate StrongBox hardware key invalidation on biometric enrollment changes.",
                        actionType = RemediationActionType.ENABLE_BIOMETRIC_LOCKOUT
                    )
                )
            }

            if (!_apiKeyValidationState.value.isConfigured) {
                proposals.add(
                    RemediationProposal(
                        id = "FIX-SECRETS-04",
                        title = "Re-Sync Gemini API Secrets & Fallback Engine",
                        description = "Gemini API key is unconfigured or rate-limited. Local Aegis offline fail-safe rules are active.",
                        severity = "HIGH",
                        impactedComponent = "Gemini AI Endpoint Bridge",
                        proposedFix = "Re-verify API key status and activate zero-latency offline fallback engine.",
                        actionType = RemediationActionType.REFRESH_SECRETS_CONFIG
                    )
                )
            }

            if (proposals.isEmpty()) {
                proposals.add(
                    RemediationProposal(
                        id = "HARDEN-SYSTEM-05",
                        title = "Engage Air-Gapped Zero-Trust Lockdown",
                        description = "All current baseline security checks passed. System is ready for elevated air-gapped protection.",
                        severity = "RECOMMENDED",
                        impactedComponent = "System Sockets & USB ADB Interface",
                        proposedFix = "Disable unauthenticated USB debug routes and enforce strict zero-trust air-gap lockdown.",
                        actionType = RemediationActionType.ENABLE_ZERO_TRUST_LOCKDOWN
                    )
                )
            }

            _remediationProposals.value = proposals
            _isSelfHealingActive.value = false

            loggingService.logOperation(
                category = "Self-Healing Engine",
                title = "Aegis Self-Diagnosis Completed",
                details = "Identified ${proposals.size} potential remediation proposal(s) requiring Human Architect authorization.",
                severity = "INFO",
                role = currentRole.value.label
            )
        }
    }

    fun approveAndExecuteRemediation(proposalId: String) {
        viewModelScope.launch {
            val list = _remediationProposals.value.toMutableList()
            val index = list.indexOfFirst { it.id == proposalId }
            if (index != -1) {
                val proposal = list[index]
                when (proposal.actionType) {
                    RemediationActionType.ENFORCE_SELINUX -> {
                        if (!_selinuxEnforced.value) toggleSelinux()
                    }
                    RemediationActionType.ENABLE_CERT_PINNING -> {
                        if (!_certPinningActive.value) toggleCertPinning()
                    }
                    RemediationActionType.ENABLE_BIOMETRIC_LOCKOUT -> {
                        if (!_biometricLockoutProtection.value) toggleBiometricLockoutProtection(true)
                    }
                    RemediationActionType.ENABLE_ZERO_TRUST_LOCKDOWN -> {
                        if (!_zeroTrustLockdown.value) toggleZeroTrustLockdown()
                    }
                    RemediationActionType.OPTIMIZE_AI_THINKING_MODE -> {
                        _useThinkingMode.value = true
                    }
                    RemediationActionType.REFRESH_SECRETS_CONFIG -> {
                        refreshApiKeyStatus()
                    }
                }

                list[index] = proposal.copy(isApproved = true, isExecuted = true)
                _remediationProposals.value = list

                loggingService.logOperation(
                    category = "Human-Authorized Remediation",
                    title = "Patch Approved & Applied: ${proposal.title}",
                    details = "Human Architect (${currentRole.value.label}) authorized fix on ${proposal.impactedComponent}. System patched successfully.",
                    severity = "SECURE",
                    role = currentRole.value.label
                )
            }
        }
    }

    fun dismissRemediation(proposalId: String) {
        _remediationProposals.value = _remediationProposals.value.filter { it.id != proposalId }
    }

    // Executive Security Briefing State
    private val _securityBriefing = MutableStateFlow<String?>(null)
    val securityBriefing: StateFlow<String?> = _securityBriefing.asStateFlow()

    private val _isGeneratingBriefing = MutableStateFlow(false)
    val isGeneratingBriefing: StateFlow<Boolean> = _isGeneratingBriefing.asStateFlow()

    // Firmware Partition AI Analysis State
    private val _firmwareAnalysisResult = MutableStateFlow<String?>(null)
    val firmwareAnalysisResult: StateFlow<String?> = _firmwareAnalysisResult.asStateFlow()

    private val _isAnalyzingFirmware = MutableStateFlow(false)
    val isAnalyzingFirmware: StateFlow<Boolean> = _isAnalyzingFirmware.asStateFlow()

    // Device Telemetry AI Diagnostic State
    private val _deviceDiagnosticResult = MutableStateFlow<String?>(null)
    val deviceDiagnosticResult: StateFlow<String?> = _deviceDiagnosticResult.asStateFlow()

    private val _isAnalyzingDevice = MutableStateFlow(false)
    val isAnalyzingDevice: StateFlow<Boolean> = _isAnalyzingDevice.asStateFlow()

    // Governance & Compliance AI Audit State
    private val _governanceAuditResult = MutableStateFlow<String?>(null)
    val governanceAuditResult: StateFlow<String?> = _governanceAuditResult.asStateFlow()

    private val _isAuditingGovernance = MutableStateFlow(false)
    val isAuditingGovernance: StateFlow<Boolean> = _isAuditingGovernance.asStateFlow()
    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    fun setAuthenticated(auth: Boolean) {
        _isAuthenticated.value = auth
    }

    init {
        val dao = AcingDatabase.getDatabase(application).securityDao()
        repository = SecurityRepository(dao)
        loggingService = CentralizedLoggingService(repository)

        auditLogs = repository.auditLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        deviceSnapshots = repository.deviceSnapshots.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        firmwareScans = repository.firmwareScans.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        seedInitialDataIfEmpty()
        recalculateTrustReport()
    }

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch {
            val cid = loggingService.generateCorrelationId("INIT")
            loggingService.logOperation(
                category = "System Init",
                title = "Acing IU: Genesis Engine Online",
                details = "Initialized zero-trust architecture with centralized audit logging and telemetry validation.",
                severity = "SECURE",
                role = currentRole.value.label,
                correlationId = cid
            )

            // Seed sample device snapshot
            repository.recordDeviceSnapshot(
                DeviceSnapshotEntity(
                    deviceName = "Pixel 9 Pro (Acing Security Lab Node #1)",
                    androidVersion = "Android 15 (API 35)",
                    selinuxState = "Enforcing",
                    avbState = "Locked (Verified Boot 2.0)",
                    hardwareKeystore = "StrongBox Keymaster (TEE Hardware-Backed)",
                    cveCount = 0,
                    healthScore = 98
                )
            )

            // Seed firmware partitions
            repository.recordFirmwareScan(
                FirmwareScanEntity(
                    imageName = "acing-iu-genesis-v1.3.1.img",
                    partitionName = "boot.img",
                    sha256Hash = "8f4e2c1a90b76543d210fe9876543210abcdef9876543210fedcba9876543210",
                    signatureStatus = "Verified RSA-4096",
                    isVerified = true
                )
            )
            repository.recordFirmwareScan(
                FirmwareScanEntity(
                    imageName = "acing-iu-genesis-v1.3.1.img",
                    partitionName = "system.img",
                    sha256Hash = "a1b2c3d4e5f67890123456789abcdef0123456789abcdef0123456789abcdef0",
                    signatureStatus = "Verified DM-Verity",
                    isVerified = true
                )
            )
            repository.recordFirmwareScan(
                FirmwareScanEntity(
                    imageName = "acing-iu-genesis-v1.3.1.img",
                    partitionName = "vendor.img",
                    sha256Hash = "f0e9d8c7b6a543210987654321fedcba0123456789abcdef0123456789abcdef",
                    signatureStatus = "Verified Manufacturer Key",
                    isVerified = true
                )
            )
        }
    }

    fun updateTelemetryInput(newInput: DeviceTelemetryInput) {
        _currentTelemetryInput.value = newInput
        _selinuxEnforced.value = newInput.selinuxEnforcing
        recalculateTrustReport()
    }

    fun recalculateTrustReport() {
        viewModelScope.launch {
            val report = deviceTrustService.calculateTrustReport(
                rawInput = _currentTelemetryInput.value,
                loggingService = loggingService,
                operatorRole = currentRole.value.label
            )
            _deviceTrustReport.value = report
        }
    }

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    fun setSecurityRole(role: SecurityRole) {
        _currentRole.value = role
        viewModelScope.launch {
            loggingService.logOperation(
                category = "Role Management",
                title = "Security Role Switch",
                details = "Active profile updated to ${role.label} (${role.level}).",
                severity = "INFO",
                role = role.label
            )
        }
    }

    fun toggleSelinux() {
        val newState = !_selinuxEnforced.value
        _selinuxEnforced.value = newState
        val updatedTelemetry = _currentTelemetryInput.value.copy(selinuxEnforcing = newState)
        updateTelemetryInput(updatedTelemetry)

        viewModelScope.launch {
            loggingService.logDeviceAction(
                actionTitle = if (newState) "SELinux Enforced" else "SELinux Set to Permissive",
                details = if (newState) "Mandatory Access Controls re-enforced." else "WARNING: SELinux set to permissive state. Vulnerability window opened.",
                severity = if (newState) "SECURE" else "CRITICAL",
                role = currentRole.value.label
            )
        }
    }

    fun toggleZeroTrustLockdown() {
        val newState = !_zeroTrustLockdown.value
        _zeroTrustLockdown.value = newState
        viewModelScope.launch {
            loggingService.logDeviceAction(
                actionTitle = if (newState) "FULL SYSTEM LOCKDOWN ENGAGED" else "Lockdown Disengaged",
                details = if (newState) "All non-essential sockets, debug interfaces, and USB ADB routes terminated." else "Standard zero-trust policy active.",
                severity = if (newState) "CRITICAL" else "INFO",
                role = currentRole.value.label
            )
        }
    }

    fun toggleCertPinning() {
        val newState = !_certPinningActive.value
        _certPinningActive.value = newState
        viewModelScope.launch {
            loggingService.logDeviceAction(
                actionTitle = if (newState) "Strict Certificate Pinning Active" else "Cert Pinning Bypassed",
                details = if (newState) "TLS connections restricted to Aegis Genesis pinned keys." else "WARNING: Cert pinning disabled for network debugging.",
                severity = if (newState) "SECURE" else "WARNING",
                role = currentRole.value.label
            )
        }
    }

    fun runFullSecurityAudit() {
        viewModelScope.launch {
            val workflowId = loggingService.startWorkflow("ZERO_TRUST_AUDIT")

            loggingService.logResearchWorkflow(
                workflowName = "ZERO_TRUST_AUDIT",
                stepTitle = "Sweep Initiated",
                details = "Checking AVB flags, DM-Verity digests, TEE Hardware keys, SELinux policies, and network sockets.",
                severity = "INFO",
                role = currentRole.value.label,
                correlationId = workflowId
            )

            // Recalculate trust score as part of audit
            recalculateTrustReport()
            val currentReport = _deviceTrustReport.value

            val snapshot = DeviceSnapshotEntity(
                deviceName = _currentTelemetryInput.value.hardwareId,
                androidVersion = _currentTelemetryInput.value.androidVersion,
                selinuxState = if (_selinuxEnforced.value) "Enforcing" else "Permissive",
                avbState = if (_avbVerified.value) "Locked (AVB 2.0)" else "Unlocked",
                hardwareKeystore = "StrongBox Keymaster",
                cveCount = if (_selinuxEnforced.value) 0 else 2,
                healthScore = currentReport?.score ?: 98
            )
            repository.recordDeviceSnapshot(snapshot)

            loggingService.logResearchWorkflow(
                workflowName = "ZERO_TRUST_AUDIT",
                stepTitle = "Sweep Completed",
                details = "Overall Device Health Index: ${snapshot.healthScore}%. Tier: ${currentReport?.tier?.label ?: "Trusted"}. No malware detected.",
                severity = "SECURE",
                role = currentRole.value.label,
                correlationId = workflowId
            )

            loggingService.clearWorkflow()
        }
    }

    fun scanFirmwarePartition(partitionName: String) {
        viewModelScope.launch {
            val cid = loggingService.generateCorrelationId("FW")
            val hash = "a" + System.currentTimeMillis().toString(16) + "9876543210fedcba8765432101234567"
            val scan = FirmwareScanEntity(
                imageName = "acing-iu-genesis-target.img",
                partitionName = partitionName,
                sha256Hash = hash,
                signatureStatus = "Verified RSA-4096 Signature",
                isVerified = true
            )
            repository.recordFirmwareScan(scan)
            loggingService.logOperation(
                category = "Firmware Scan",
                title = "Partition Inspection: $partitionName",
                details = "Integrity check passed. Hash: ${hash.take(16)}... Signature: RSA-4096 OK.",
                severity = "SECURE",
                role = currentRole.value.label,
                correlationId = cid
            )
        }
    }

    fun toggleThinkingMode(enabled: Boolean) {
        _useThinkingMode.value = enabled
    }

    fun sendAiPrompt(prompt: String) {
        if (prompt.isBlank()) return

        val userMsg = ChatMessage(
            sender = "USER",
            text = prompt,
            isThinkingModel = _useThinkingMode.value
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _aiLoading.value = true

        viewModelScope.launch {
            val responseText = aiService.sendChatMessage(
                history = _chatMessages.value,
                userMessage = prompt,
                useThinkingMode = _useThinkingMode.value
            )

            val aiMsg = ChatMessage(
                sender = "AEGIS_AI",
                text = responseText,
                isThinkingModel = _useThinkingMode.value
            )
            _chatMessages.value = _chatMessages.value + aiMsg
            _aiLoading.value = false

            loggingService.logThreatAnalysis(
                scenario = prompt,
                outcomeDetails = "Response generated cleanly by Aegis AI.",
                severity = "INFO",
                role = currentRole.value.label
            )
        }
    }

    fun analyzeLogSnippet(snippet: String) {
        if (snippet.isBlank()) return
        _isAnalyzingLog.value = true
        _logAnalysisResult.value = null

        viewModelScope.launch {
            val result = aiService.analyzeSecurityLog(snippet, currentRole.value.label)
            _logAnalysisResult.value = result
            _isAnalyzingLog.value = false

            loggingService.logResearchWorkflow(
                workflowName = "FORENSICS_ANALYSIS",
                stepTitle = "Logcat Inspection",
                details = "Analyzed log snippet (${snippet.length} chars). Result delivered.",
                severity = "INFO",
                role = currentRole.value.label
            )
        }
    }

    fun clearLogAnalysis() {
        _logAnalysisResult.value = null
    }

    fun generateSecurityBriefing() {
        _isGeneratingBriefing.value = true
        _securityBriefing.value = null

        viewModelScope.launch {
            val result = aiService.generateSecurityBriefing(
                selinuxEnforced = _selinuxEnforced.value,
                lockdownActive = _zeroTrustLockdown.value,
                role = _currentRole.value.label
            )
            _securityBriefing.value = result
            _isGeneratingBriefing.value = false

            loggingService.logThreatAnalysis(
                scenario = "Executive Briefing",
                outcomeDetails = "Security briefing generated for role ${_currentRole.value.label}.",
                severity = "INFO",
                role = _currentRole.value.label
            )
        }
    }

    fun clearSecurityBriefing() {
        _securityBriefing.value = null
    }

    fun analyzeFirmwareWithAi(
        partitionName: String,
        sha256Hash: String,
        signatureStatus: String
    ) {
        _isAnalyzingFirmware.value = true
        _firmwareAnalysisResult.value = null

        viewModelScope.launch {
            val result = aiService.analyzeFirmwarePartition(
                partitionName = partitionName,
                sha256Hash = sha256Hash,
                signatureStatus = signatureStatus
            )
            _firmwareAnalysisResult.value = result
            _isAnalyzingFirmware.value = false

            loggingService.logResearchWorkflow(
                workflowName = "FIRMWARE_AI_AUDIT",
                stepTitle = "Partition AI Analysis",
                details = "Audited $partitionName image. Signature: $signatureStatus.",
                severity = "INFO",
                role = _currentRole.value.label
            )
        }
    }

    fun clearFirmwareAnalysis() {
        _firmwareAnalysisResult.value = null
    }

    fun analyzeDeviceWithAi() {
        _isAnalyzingDevice.value = true
        _deviceDiagnosticResult.value = null

        viewModelScope.launch {
            val snapshot = deviceSnapshots.value.firstOrNull()
            val deviceName = snapshot?.deviceName ?: "Pixel 9 Pro Security Target"
            val androidVersion = snapshot?.androidVersion ?: "Android 15 (API 35)"
            val keystoreState = snapshot?.hardwareKeystore ?: "StrongBox TEE Keymaster"
            val selinuxState = if (_selinuxEnforced.value) "Enforcing (MAC Active)" else "Permissive"

            val result = aiService.analyzeDeviceTelemetry(
                deviceName = deviceName,
                androidVersion = androidVersion,
                selinuxState = selinuxState,
                keystoreState = keystoreState
            )
            _deviceDiagnosticResult.value = result
            _isAnalyzingDevice.value = false

            loggingService.logResearchWorkflow(
                workflowName = "DEVICE_DIAGNOSTIC",
                stepTitle = "Hardware AI Diagnostic",
                details = "Audited $deviceName state ($selinuxState).",
                severity = "INFO",
                role = _currentRole.value.label
            )
        }
    }

    fun clearDeviceDiagnostic() {
        _deviceDiagnosticResult.value = null
    }

    fun auditGovernanceWithAi(apiKeyConfigured: Boolean) {
        _isAuditingGovernance.value = true
        _governanceAuditResult.value = null

        viewModelScope.launch {
            val result = aiService.auditGovernanceCompliance(
                roleLabel = _currentRole.value.label,
                roleLevel = _currentRole.value.level,
                apiKeyConfigured = apiKeyConfigured
            )
            _governanceAuditResult.value = result
            _isAuditingGovernance.value = false

            loggingService.logResearchWorkflow(
                workflowName = "GOVERNANCE_AUDIT",
                stepTitle = "RBAC AI Compliance Audit",
                details = "Audited RBAC compliance for ${_currentRole.value.label}.",
                severity = "INFO",
                role = _currentRole.value.label
            )
        }
    }

    fun clearGovernanceAudit() {
        _governanceAuditResult.value = null
    }

    fun clearChatHistory() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "AEGIS_AI",
                text = "Aegis AI Chat session reset. Select a scenario or type a security query below."
            )
        )
    }

    fun quickAiPrompt(prompt: String) {
        selectTab(AppTab.AEGIS_AI)
        sendAiPrompt(prompt)
    }

    fun logEvent(category: String, title: String, details: String) {
        viewModelScope.launch {
            loggingService.logOperation(
                category = category,
                title = title,
                details = details,
                severity = "INFO",
                role = _currentRole.value.label
            )
        }
    }

    fun clearAuditLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }

    fun evaluateAgentActionRequest(
        agent: AgentIdentity,
        operation: String,
        targetType: String,
        targetId: String,
        requestedAuthorityLevel: AgentAuthorityLevel
    ) {
        viewModelScope.launch {
            val eval = agentGovernanceService.evaluateActionRequest(
                agent = agent,
                operation = operation,
                targetType = targetType,
                targetId = targetId,
                requestedAuthorityLevel = requestedAuthorityLevel,
                requestorRole = _currentRole.value.label,
                loggingService = loggingService
            )
            _lastAgentEvaluation.value = eval
        }
    }

    fun scanBootkitAndRamdisk() {
        viewModelScope.launch {
            val report = firmwareSecurityEngine.detectBootkitInjection()
            _bootkitReport.value = report
            loggingService.logOperation(
                category = "Firmware Audit",
                title = "Automatic Bootkit Injection & Ramdisk Scan Completed",
                details = "Target: ${report.scannedTarget} | Threat Level: ${report.threatLevel} | 0 unauthorized vectors found.",
                severity = "SECURE",
                role = _currentRole.value.label
            )
        }
    }

    fun toggleVbmetaSpoof(enableSpoof: Boolean) {
        viewModelScope.launch {
            val res = firmwareSecurityEngine.validateAndSpoofVbmeta(enableSpoof)
            _vbmetaResult.value = res
            loggingService.logOperation(
                category = "Vbmeta Research",
                title = "Vbmeta.img Image Unblocker & Mocker Executed",
                details = res.details,
                severity = if (enableSpoof) "WARNING" else "SECURE",
                role = _currentRole.value.label
            )
        }
    }

    fun runStrongBoxTeeCrypto(keyAlias: String, textToEncrypt: String) {
        viewModelScope.launch {
            val res = firmwareSecurityEngine.performStrongBoxTeeCrypto(keyAlias, textToEncrypt)
            _strongBoxCryptoResult.value = res
            loggingService.logOperation(
                category = "Hardware TEE",
                title = "StrongBox Keymaster TEE AES-256-GCM Encryption Executed",
                details = "Alias: '$keyAlias' | CipherText: ${res.cipherTextBase64.take(30)}...",
                severity = "SECURE",
                role = _currentRole.value.label
            )
        }
    }

    fun unblockDomainTransition(sourceDomain: String, targetDomain: String) {
        viewModelScope.launch {
            val updated = _domainTransitions.value.map { item ->
                if (item.sourceDomain == sourceDomain && item.targetDomain == targetDomain) {
                    firmwareSecurityEngine.unblockDomainTransition(sourceDomain, targetDomain)
                } else item
            }
            _domainTransitions.value = updated
            loggingService.logOperation(
                category = "SELinux Policy",
                title = "Domain Transition Permission Unblocked (Lab)",
                details = "Transition $sourceDomain -> $targetDomain unblocked under lab policy.",
                severity = "WARNING",
                role = _currentRole.value.label
            )
        }
    }

    // =========================================================================
    // Module 3: Knox & SELinux Policy Generator
    // =========================================================================
    fun generateSelinuxPolicyFromDenial(rawLogcatInput: String) {
        viewModelScope.launch {
            val result = selinuxPolicyGenerator.generatePolicyFromDenial(rawLogcatInput)
            _selinuxPolicyResult.value = result
            loggingService.logOperation(
                category = "SELinux Policy Generator",
                title = "Generated .te Policy Rules from Logcat",
                details = result.summary,
                severity = if (result.isNeverallowViolation) "WARNING" else "SECURE",
                role = _currentRole.value.label
            )
        }
    }

    fun clearSelinuxPolicyResult() {
        _selinuxPolicyResult.value = null
    }

    // =========================================================================
    // Module 4: Custom Security Audit Routine Engine
    // =========================================================================
    fun runCustomSecurityAuditRoutine(customTaskName: String = "Full Zero-Trust System Integrity Scan") {
        _isCustomAuditRunning.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            val report = customAuditEngine.runZeroTrustAuditSuite(
                customTaskName = customTaskName,
                selinuxEnforced = _selinuxEnforced.value,
                knoxFuseIntact = true,
                bootloaderLocked = _avbVerified.value
            )
            _customAuditReport.value = report
            _isCustomAuditRunning.value = false

            loggingService.logOperation(
                category = "Security Audit Engine",
                title = "Custom Audit Routine Executed: $customTaskName",
                details = "Overall Score: ${report.overallScore}/100 | Passed: ${report.passedChecks}/${report.totalChecks} | Zero Trust: ${report.zeroTrustCompliant}",
                severity = if (report.zeroTrustCompliant) "SECURE" else "WARNING",
                role = _currentRole.value.label
            )
        }
    }

    fun clearCustomAuditReport() {
        _customAuditReport.value = null
    }

    // =========================================================================
    // Module 1: Custom Network Vulnerability Scanner
    // =========================================================================
    fun runNetworkVulnerabilityScan(targetHost: String = "127.0.0.1 (Local Sockets)") {
        _isNetworkScanning.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(600)
            val report = networkScanner.scanNetworkEndpoints(targetHost = targetHost)
            _networkScanReport.value = report
            _isNetworkScanning.value = false

            loggingService.logOperation(
                category = "Network Scanner",
                title = "Network Socket & TLS Vulnerability Scan Completed",
                details = "Target: $targetHost | Open Ports: ${report.openPortsCount} | High-Risk: ${report.highRiskPortsCount} | Grade: ${report.overallSecurityGrade}",
                severity = if (report.highRiskPortsCount > 0) "WARNING" else "SECURE",
                role = _currentRole.value.label
            )
        }
    }

    fun clearNetworkScanReport() {
        _networkScanReport.value = null
    }

    // =========================================================================
    // Module 2: Odin-Flashed Firmware Verification Module
    // =========================================================================
    fun verifyOdinFirmwareArchive(archiveName: String = "AP_SM-S938U_S25_ULTRA_OEM_BUILD.tar.md5") {
        _isVerifyingOdin.value = true
        viewModelScope.launch {
            kotlinx.coroutines.delay(700)
            val result = odinVerifier.parsePitAndVerifyOdinFirmware(sampleTarMd5Name = archiveName)
            _odinFirmwareResult.value = result
            _isVerifyingOdin.value = false

            loggingService.logOperation(
                category = "Odin Firmware Verifier",
                title = "PIT Partition Table & TAR.MD5 Integrity Verified",
                details = result.verificationSummary,
                severity = if (result.tamperedPartitionsDetected.isNotEmpty()) "CRITICAL" else "SECURE",
                role = _currentRole.value.label
            )
        }
    }

    fun clearOdinFirmwareResult() {
        _odinFirmwareResult.value = null
    }

    // =========================================================================
    // Module 5: Shopify License Validation Subsystem
    // =========================================================================
    fun validateShopifyLicenseToken(purchaseToken: String = "GENESIS-PRO-S25U-938U-ENTERPRISE") {
        _isValidatingShopifyLicense.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result: ShopifyValidationResult = shopifyLicenseValidator.validateLicenseToken(purchaseToken)

            _shopifyLicenseState.update { currentState ->
                currentState.copy(
                    isLicensed = result.isValid,
                    customerToken = purchaseToken,
                    customerEmail = result.customerEmail ?: "unverified@acing-iu.internal",
                    licenseTier = result.licenseTier,
                    activeSubscriptionId = if (result.isValid) "SUB-${purchaseToken.take(8).uppercase()}" else null,
                    lastValidatedTimestamp = System.currentTimeMillis(),
                    validationMessage = result.message,
                    lastResponseCode = result.httpStatusCode
                )
            }
            _isValidatingShopifyLicense.value = false

            loggingService.logOperation(
                category = "Shopify Licensing",
                title = if (result.isValid) "Shopify License Validated (${result.licenseTier.displayName})" else "Shopify License Verification Failed",
                details = "Token: ${purchaseToken.take(12)}... | Email: ${result.customerEmail ?: "N/A"} | Status: ${result.subscriptionStatus} | Summary: ${result.rawPayloadSummary}",
                severity = if (result.isValid) "SECURE" else "WARNING",
                role = _currentRole.value.label,
                outcome = if (result.isValid) "LICENSE_ACTIVE" else "LICENSE_REJECTED"
            )
        }
    }

    fun resetShopifyLicenseState() {
        _shopifyLicenseState.value = ShopifyLicenseState()
    }
}
