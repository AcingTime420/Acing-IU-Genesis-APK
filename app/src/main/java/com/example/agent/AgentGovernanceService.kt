package com.example.agent

import com.example.logging.CentralizedLoggingService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

sealed class AgentActionEvaluation {
    data class AutoApproved(
        val agent: AgentIdentity,
        val approvalRecord: AgentApprovalRecord,
        val policyReason: String
    ) : AgentActionEvaluation()

    data class RequiresHumanApproval(
        val agent: AgentIdentity,
        val approvalRecord: AgentApprovalRecord,
        val warningDetails: String
    ) : AgentActionEvaluation()

    data class Denied(
        val agent: AgentIdentity,
        val denialReason: String,
        val codeOfConductArticle: String
    ) : AgentActionEvaluation()
}

class AgentGovernanceService {

    suspend fun evaluateActionRequest(
        agent: AgentIdentity,
        operation: String,
        targetType: String,
        targetId: String,
        requestedAuthorityLevel: AgentAuthorityLevel,
        requestorRole: String,
        loggingService: CentralizedLoggingService
    ): AgentActionEvaluation {
        val cid = loggingService.generateCorrelationId("AGNT")
        val reqId = "REQ-" + UUID.randomUUID().toString().take(8).uppercase()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val nowStr = dateFormat.format(Date())
        val expireStr = dateFormat.format(Date(System.currentTimeMillis() + 3600 * 1000))

        // 1. Check if requested authority level exceeds agent's maximum granted authority level
        if (requestedAuthorityLevel.levelNumber > agent.maxAuthorityLevel.levelNumber) {
            val denialReason = "Requested Authority Level ${requestedAuthorityLevel.levelNumber} (${requestedAuthorityLevel.title}) exceeds maximum agent authorization cap Level ${agent.maxAuthorityLevel.levelNumber} (${agent.maxAuthorityLevel.title})."
            loggingService.logOperation(
                category = "Agent Governance",
                title = "Agent Policy Violation: ${agent.displayName}",
                details = denialReason,
                severity = "CRITICAL",
                role = requestorRole,
                correlationId = cid
            )
            return AgentActionEvaluation.Denied(
                agent = agent,
                denialReason = denialReason,
                codeOfConductArticle = "Article 3 — Least Privilege & Assigned Role Caps"
            )
        }

        // 2. Check for Prohibited Operations (Code of Conduct Guardrails)
        val lowerOp = operation.lowercase()
        if (lowerOp.contains("bypass_selinux") ||
            lowerOp.contains("disable_audit") ||
            lowerOp.contains("extract_raw_keys") ||
            lowerOp.contains("retaliate") ||
            lowerOp.contains("fake_test_results")
        ) {
            val denialReason = "Operation '$operation' violates Acing IU Prohibited Agent Behaviors policy."
            loggingService.logOperation(
                category = "Agent Governance",
                title = "PROHIBITED AGENT BEHAVIOR BLOCKED: ${agent.displayName}",
                details = denialReason,
                severity = "CRITICAL",
                role = requestorRole,
                correlationId = cid
            )
            return AgentActionEvaluation.Denied(
                agent = agent,
                denialReason = denialReason,
                codeOfConductArticle = "Article 4 & Prohibited Behaviors — No Unauthorized Circumvention or Audit Disabling"
            )
        }

        // 3. Level 0 to Level 2 Operations -> Auto-Approved with Immutable Audit Trail
        if (requestedAuthorityLevel.levelNumber <= 2) {
            val record = AgentApprovalRecord(
                requestId = reqId,
                agentId = agent.id,
                agentVersion = agent.version,
                requestedBy = requestorRole,
                targetType = targetType,
                targetId = targetId,
                operation = operation,
                authorityLevel = requestedAuthorityLevel.levelNumber,
                authorizationReference = "GENESIS-POLICY-AUTO-01",
                riskClassification = "LOW",
                dataLossPossible = false,
                backupRequired = false,
                approvalRequired = false,
                approvalStatus = "APPROVED",
                approvedBy = "Aegis Policy Engine (Auto)",
                approvedAtUtc = nowStr,
                expiresAtUtc = expireStr,
                correlationId = cid
            )

            loggingService.logOperation(
                category = "Agent Governance",
                title = "Agent Operation Executed: ${agent.displayName}",
                details = "Op: '$operation' | Level: ${requestedAuthorityLevel.levelNumber} (${requestedAuthorityLevel.title}) | Status: APPROVED",
                severity = "SECURE",
                role = requestorRole,
                correlationId = cid
            )

            return AgentActionEvaluation.AutoApproved(
                agent = agent,
                approvalRecord = record,
                policyReason = "Authority Level ${requestedAuthorityLevel.levelNumber} satisfies low-risk policy threshold. Operation logged."
            )
        }

        // 4. Level 3 to Level 5 Operations -> Requires Human Approval Record
        val riskClass = if (requestedAuthorityLevel.levelNumber >= 4) "HIGH" else "MODERATE"
        val dataLoss = requestedAuthorityLevel.levelNumber >= 4
        val backupReq = requestedAuthorityLevel.levelNumber >= 3

        val record = AgentApprovalRecord(
            requestId = reqId,
            agentId = agent.id,
            agentVersion = agent.version,
            requestedBy = requestorRole,
            targetType = targetType,
            targetId = targetId,
            operation = operation,
            authorityLevel = requestedAuthorityLevel.levelNumber,
            authorizationReference = "PENDING-HUMAN-APPROVAL-REF",
            riskClassification = riskClass,
            dataLossPossible = dataLoss,
            backupRequired = backupReq,
            approvalRequired = true,
            approvalStatus = "PENDING",
            approvedBy = "Awaiting $requestorRole Sign-off",
            approvedAtUtc = nowStr,
            expiresAtUtc = expireStr,
            correlationId = cid
        )

        loggingService.logOperation(
            category = "Agent Governance",
            title = "Approval Required for Agent: ${agent.displayName}",
            details = "High-impact Op: '$operation' (Level ${requestedAuthorityLevel.levelNumber}). Awaiting explicit human approval.",
            severity = "WARNING",
            role = requestorRole,
            correlationId = cid
        )

        return AgentActionEvaluation.RequiresHumanApproval(
            agent = agent,
            approvalRecord = record,
            warningDetails = "Authority Level ${requestedAuthorityLevel.levelNumber} (${requestedAuthorityLevel.title}) requires explicit human operator sign-off and valid authorization reference before execution."
        )
    }
}
