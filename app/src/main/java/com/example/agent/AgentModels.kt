package com.example.agent

enum class AgentAuthorityLevel(
    val levelNumber: Int,
    val title: String,
    val description: String,
    val approvalRequired: Boolean
) {
    LEVEL_0(0, "Explain Only", "Answer questions, explain architecture, summarize policies.", false),
    LEVEL_1(1, "Read & Analyze", "Inspect code, logs, telemetry, or firmware images.", false),
    LEVEL_2(2, "Draft", "Prepare patches, reports, or workflow execution plans.", false),
    LEVEL_3(3, "Reversible Execution", "Run unit tests, create working branch, generate build artifacts.", true),
    LEVEL_4(4, "Controlled Material Change", "Update system config, deploy to staging, operate on test device.", true),
    LEVEL_5(5, "High-Risk Operation", "Flash firmware, erase partitions, revoke fleet access, release production software.", true)
}

data class AgentIdentity(
    val id: String,
    val displayName: String,
    val version: String = "1.0.0",
    val owner: String = "Acing IU: Genesis Governance Board",
    val purpose: String,
    val assignedRole: String,
    val grantedPermissions: List<String>,
    val approvedTools: List<String>,
    val maxAuthorityLevel: AgentAuthorityLevel,
    val isEnabled: Boolean = true
)

data class AgentApprovalRecord(
    val requestId: String,
    val agentId: String,
    val agentVersion: String = "1.0.0",
    val requestedBy: String,
    val targetType: String,
    val targetId: String,
    val operation: String,
    val authorityLevel: Int,
    val authorizationReference: String,
    val riskClassification: String,
    val dataLossPossible: Boolean,
    val backupRequired: Boolean,
    val approvalRequired: Boolean,
    val approvalStatus: String, // "APPROVED", "REJECTED", "PENDING"
    val approvedBy: String,
    val approvedAtUtc: String,
    val expiresAtUtc: String,
    val policyVersion: String = "Acing-Genesis-v2.0",
    val correlationId: String
)

object GenesisAgentRoster {
    val ALL_AGENTS = listOf(
        AgentIdentity(
            id = "orchestrator-agent",
            displayName = "Genesis Orchestrator Agent",
            purpose = "Coordinate approved multi-step workflows across systems.",
            assignedRole = "Workflow Coordinator",
            grantedPermissions = listOf("workflow.read", "workflow.dispatch", "policy.check"),
            approvedTools = listOf("WorkflowRouter", "PolicyChecker", "AuditCollector"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_3
        ),
        AgentIdentity(
            id = "identity-agent",
            displayName = "Identity Agent",
            purpose = "Assist with account, RBAC role, and session administration.",
            assignedRole = "Access Manager",
            grantedPermissions = listOf("identity.read", "role.verify", "session.audit"),
            approvedTools = listOf("RoleAuditor", "SessionInspector"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_2
        ),
        AgentIdentity(
            id = "device-trust-agent",
            displayName = "Device Trust Agent",
            purpose = "Evaluate registered device posture and attestation signals.",
            assignedRole = "Trust Evaluator",
            grantedPermissions = listOf("telemetry.read", "attestation.verify", "trust.score"),
            approvedTools = listOf("TelemetryValidator", "TrustScoreEngine", "AttestationChecker"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_2
        ),
        AgentIdentity(
            id = "firmware-agent",
            displayName = "Firmware Research Agent",
            purpose = "Inspect, hash, classify, and compare Android firmware images.",
            assignedRole = "Firmware Inspector",
            grantedPermissions = listOf("firmware.read", "firmware.hash", "partition.inspect"),
            approvedTools = listOf("Sha256Hasher", "PartitionScanner", "AVBSignatureVerifier"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_2
        ),
        AgentIdentity(
            id = "workflow-agent",
            displayName = "Workflow Agent",
            purpose = "Guide controlled device research and diagnostic operations.",
            assignedRole = "Device Operations Assistant",
            grantedPermissions = listOf("device.ops.plan", "sequence.validate"),
            approvedTools = listOf("StepSequencer", "PrerequisiteValidator"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_3
        ),
        AgentIdentity(
            id = "forensic-agent",
            displayName = "Forensic Agent",
            purpose = "Support authorized investigations on evidence copies.",
            assignedRole = "Forensic Analyst",
            grantedPermissions = listOf("evidence.read", "timeline.build", "log.parse"),
            approvedTools = listOf("LogcatTimelineParser", "HashComparer", "EvidenceInspector"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_2
        ),
        AgentIdentity(
            id = "security-agent",
            displayName = "Security Agent",
            purpose = "Triage threats, analyze vulnerabilities, and recommend fixes.",
            assignedRole = "Threat Analyst",
            grantedPermissions = listOf("threat.triage", "vuln.scan", "policy.eval"),
            approvedTools = listOf("ThreatModeler", "SELinuxLogAnalyzer", "VulnerabilityMapper"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_2
        ),
        AgentIdentity(
            id = "build-agent",
            displayName = "Build Agent",
            purpose = "Compile, run local unit tests, and package release artifacts.",
            assignedRole = "Build Engineer",
            grantedPermissions = listOf("build.execute", "test.run", "package.create"),
            approvedTools = listOf("GradleRunner", "RobolectricTestEngine", "CompilerValidator"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_3
        ),
        AgentIdentity(
            id = "release-agent",
            displayName = "Release Agent",
            purpose = "Prepare software releases, generate SBOMs, and verify checksums.",
            assignedRole = "Release Manager",
            grantedPermissions = listOf("release.prepare", "sbom.generate", "checksum.sign"),
            approvedTools = listOf("SbomGenerator", "ChecksumSigner", "ReleaseNotesBuilder"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_4
        ),
        AgentIdentity(
            id = "documentation-agent",
            displayName = "Documentation Agent",
            purpose = "Maintain architecture records, tutorials, and technical docs.",
            assignedRole = "Technical Writer",
            grantedPermissions = listOf("docs.read", "docs.write", "spec.audit"),
            approvedTools = listOf("MarkdownFormatter", "SpecAuditor", "DocGenerator"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_2
        ),
        AgentIdentity(
            id = "education-agent",
            displayName = "Education Agent",
            purpose = "Produce structured security learning labs and assessments.",
            assignedRole = "Security Instructor",
            grantedPermissions = listOf("lab.generate", "explanation.build"),
            approvedTools = listOf("LabGenerator", "ConceptExplainer"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_1
        ),
        AgentIdentity(
            id = "compliance-agent",
            displayName = "Compliance Agent",
            purpose = "Evaluate RBAC controls, security logs, and regulatory evidence.",
            assignedRole = "Compliance Auditor",
            grantedPermissions = listOf("audit.read", "compliance.eval"),
            approvedTools = listOf("AuditLogEvaluator", "ControlMapper"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_1
        ),
        AgentIdentity(
            id = "incident-agent",
            displayName = "Incident Response Agent",
            purpose = "Assist during security incidents with rapid fact collection.",
            assignedRole = "Incident Handler",
            grantedPermissions = listOf("incident.triage", "containment.plan"),
            approvedTools = listOf("AlertTriager", "ContainmentPlanner"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_2
        ),
        AgentIdentity(
            id = "privacy-agent",
            displayName = "Privacy Agent",
            purpose = "Minimize collected data and audit retention/redaction rules.",
            assignedRole = "Privacy Inspector",
            grantedPermissions = listOf("data.minimize", "redact.audit"),
            approvedTools = listOf("RedactionFilter", "RetentionPolicyChecker"),
            maxAuthorityLevel = AgentAuthorityLevel.LEVEL_1
        )
    )
}
