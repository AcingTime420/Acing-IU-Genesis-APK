# Acing IU: Genesis — Current Specifications

Acing IU: Genesis is specified as a **security-first Android firmware research, device-assurance, identity, policy, auditing, and education platform**.

Its governing principle is:

> Every user, device, firmware artifact, connection, and privileged operation must be identified, verified, authorized, recorded, and capable of being audited.

These specifications describe the intended architecture. Some foundation components exist, while advanced firmware, forensic, AI, and enterprise features remain under development.

---

## 1. Product Specification

| Field | Specification |
| :--- | :--- |
| Product | Acing IU: Genesis |
| Product category | Android security research and device-assurance platform |
| Architecture | API gateway with independently deployable backend services |
| Primary interface | Next.js web-based Security Center |
| Backend | .NET 8 services |
| Primary database | PostgreSQL 16 |
| Cache and session services | Redis 7 |
| Deployment | Docker Compose, with future production orchestration |
| Initial reference device | Samsung Galaxy S25 Ultra SM-S938U, Verizon variant |
| Security model | Zero trust, least privilege and continuous verification |
| Authorization | RBAC with planned ABAC policy evaluation |
| Authentication | JWT, rotating refresh tokens, TOTP MFA |
| Auditing | Structured security-event records |
| Distribution targets | Web platform, Windows installer and future bootable research environment |
| Intended users | Security researchers, developers, device technicians, administrators, educators and authorized forensic analysts |

---

## 2. Core Purpose

Genesis is intended to provide one controlled environment for:

- Android device identification
- Device inventory and enrollment
- Security-posture assessment
- Firmware collection and verification
- Firmware and partition research
- Authorized maintenance and recovery
- Application-security analysis
- Defensive forensic investigation
- Policy-controlled privileged operations
- Security reporting
- Technical documentation and training
- AI-assisted analysis and workflow guidance

It is not supposed to operate as an unrestricted bypass utility. High-risk operations should require authorization, compatible hardware, documented ownership, safety checks, backups and audit records.

---

## 3. System Architecture

```
[Next.js Security Center] --> [API Gateway]
                                 |---> [Identity Service] ----> [PostgreSQL / Redis]
                                 |---> [Device Trust Service] -> [PostgreSQL]
                                 |---> [Policy Engine] --------> [PostgreSQL / Redis]
                                 |---> [Audit Service] --------> [PostgreSQL]
```

### Frontend Security Center
The frontend is specified to provide:
- Authentication and account management
- Device inventory
- Device-trust dashboards
- Security alerts
- Audit-log inspection
- Firmware-research interfaces
- Administrative policy controls
- Research workflow visualization
- Project backlog and documentation views
- Authorized ADB and diagnostic workflows
- Future AI-assisted recommendations

Current frontend technology: Next.js, React, TypeScript, Tailwind-style utility classes, Recharts, ESLint, npm-locked dependencies.

### API Gateway
The gateway is the central ingress point.
Specified responsibilities: TLS termination, Request routing, JWT validation, Security-header enforcement, CORS controls, Rate limiting, Request-size limits, Correlation identifiers, Centralized error handling, Policy checks, Audit-event initiation.

---

## 4. Identity Service

**Current documented port:** `8080`

| Method | Endpoint | Purpose |
| :--- | :--- | :--- |
| POST | `/api/auth/register` | Register an account |
| POST | `/api/auth/login` | Authenticate and issue tokens |
| POST | `/api/auth/refresh` | Rotate refresh tokens |
| POST | `/api/auth/logout` | Revoke sessions and token families |
| GET | `/api/auth/me` | Return the authenticated profile |
| GET | `/api/auth/mfa/enroll` | Begin TOTP enrollment |
| POST | `/api/auth/mfa/verify` | Verify and enable MFA |
| GET | `/health/live` | Process-liveness check |
| GET | `/health/ready` | Dependency-readiness check |

Security requirements: Argon2id password hashing, Short-lived JWT access tokens, Rotating refresh tokens, Refresh-token reuse detection, Redis-backed access-token revocation, HTTP-only refresh cookies, TOTP MFA, Recovery codes, Rate limiting.

---

## 5. Authorization & Policy Model

Genesis specifies both role-based (RBAC) and attribute-based (ABAC) access decisions.

### Initial Roles
- Administrator
- Operator
- Researcher / Authorized User
- Standard User
- Future Auditor & Instructor roles

### Context-Aware Policy Formula
$$\text{Decision} = f(\text{Identity}, \text{Role}, \text{Permission}, \text{MFA}, \text{Device Trust}, \text{Operation}, \text{Context})$$

---

## 6. Device Trust Service

**Current documented port:** `8081`

| Method | Endpoint | Purpose |
| :--- | :--- | :--- |
| POST | `/api/trust/telemetry/submit` | Submit device-health signals |
| GET | `/api/trust/devices/{hardwareId}` | Retrieve a device |
| GET | `/api/trust/devices` | List registered devices |
| GET | `/health/live` | Liveness check |
| GET | `/health/ready` | Readiness check |

### Foundational Trust Scoring Baseline
- SELinux enforcing: +40
- Bootloader locked: +30
- Partitions unmodified: +20
- Knox warranty fuse intact: +10
- Root detected: Force score to 0

**Access Threshold:** 80  
**Tiers:** Trusted (90–100), Elevated (70–89), Restricted (40–69), Quarantined (0–39).

---

## 7. Device Telemetry Specification
Includes Device UUID, Hardware ID, Model, Android Version, Patch Level, Build Fingerprint, Bootloader state, SELinux state, Verified Boot state, Knox warranty state, Attestation result, Firmware hashes, and Trust classification.

---

## 8. Initial Hardware Baseline
- **Model:** Samsung Galaxy S25 Ultra (SM-S938U, Verizon Variant)
- **SoC:** Qualcomm Snapdragon 8 Elite
- Signals evaluated include AVB state, Knox Vault availability, hardware-backed key attestation, and signed firmware provenance.

---

## 9. Firmware Research Specification
Supports firmware package ingestion, hashing, AP/BL/CP/CSC identification, partition-map discovery, boot image inspection, AVB metadata analysis, and version/rollback checks.

---

## 10. Digital Forensics & Audit Specifications
Defensive forensics module for evidence acquisition, hash verification, timeline reconstruction, logcat analysis, and immutable audit log streams with UTC timestamps, Correlation IDs, and User/Device tracking.

---

## 11. Current Implementation Boundary
- **Demonstrated**: Android Client (Compose/MVVM), Identity API foundation, JWT/Refresh token flows, Device Trust API, PostgreSQL migrations, Redis cache, NGINX gateway config, CI/CD pipeline, Next.js frontend, Windows installer.
- **Under Active Engineering**: Hardware-backed Knox attestation at fleet scale, complete ABAC policy engine, full multi-device flashing pipeline, court-defensible forensic suite.

---

## 12. Concise Official Specification

> **Acing IU: Genesis is a modular, zero-trust Android security research and device-assurance platform. It combines identity protection, device enrollment, integrity assessment, firmware intelligence, controlled maintenance workflows, defensive forensic analysis, policy enforcement, auditability, AI-assisted research, and technical education. Its operations are designed for authorized use, least privilege, reproducibility, evidence integrity, privacy, and human-controlled execution.**
