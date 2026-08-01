# Acing IU: Genesis Cookbooks — Series Master Blueprint & Assessment
**Author and Project Lead:** Micki Hart (Independent Software Engineer & Cybersecurity Researcher)  
**Document Version:** 1.0.0  
**Status:** Approved Master Baseline  
**Classification:** Technical Blueprint & Strategic Assessment  

---

## 1. Assessment of Supplied Acing IU: Genesis Materials

### 1.1 Source Inventory Breakdown
An exhaustive audit of the `Acing-IU-Genesis` workspace reveals a modular, security-focused Android application built with Kotlin, Jetpack Compose (Material 3), and Clean MVVM architecture.

| Package Path | Core Classes / File Artifacts | Primary Responsibility & Status |
| :--- | :--- | :--- |
| `com.example.firmware` | `BuildPropAndFirmwareSecurityEngine.kt` | Firmware parsing, `build.prop` auditing, Knox/SELinux/VBMeta state checks. *(Confirmed in source)* |
| `com.example.trust` | `DeviceTrustService.kt` | Device integrity verification, hardware attestation, Knox/TEE status tracking. *(Confirmed in source)* |
| `com.example.agent` | `AgentGovernanceService.kt`, `AgentModels.kt` | Policy enforcement, rule-based governance, agent action auditing. *(Confirmed in source)* |
| `com.example.security` | `TelemetryValidator.kt` | Cryptographic signature validation, telemetry payload integrity verification. *(Confirmed in source)* |
| `com.example.logging` | `CentralizedLoggingService.kt` | Immutable audit logging, structured security event storage. *(Confirmed in source)* |
| `com.example.ui.screens` | `DashboardScreen.kt`, `DevicesScreen.kt`, `FirmwareScreen.kt`, `ForensicsScreen.kt`, `GovernanceScreen.kt`, `AegisAiScreen.kt` | Laboratory UI dashboards, real-time diagnostic visualizers, policy controls. *(Confirmed in source)* |
| `com.example.ui` | `AcingViewModel.kt` | Central state management orchestrating trust, telemetry, firmware, and governance flows. *(Confirmed in source)* |

### 1.2 Architectural & Capability Classification
- **Confirmed in Current Source**: Android Jetpack Compose frontend, ViewModel state management, mock/local security inspection engines, immutable log buffer, telemetry validation routines.
- **Documented / Lab-Facing**: Controlled UI surface recipes for VBMeta analysis, bootkit detection heuristics, SELinux domain transition diagnostics, Knox container state inspection.
- **Planned / Future Roadmap**: .NET 8 backend microservices (Identity API, Device Trust API), PostgreSQL schemas, Redis caching, NGINX API Gateway, Docker Compose deployment configurations.

---

## 2. Proposed Three-Book Series Blueprint

### Book One: Foundations, Architecture, and Workstation Cookbook
Focuses on developer environment setup, Windows/PowerShell/Linux toolchains, repository hygiene, Git workflows, Docker fundamentals, and .NET/Android build toolchain establishment.

### Book Two: Android Security Research and Device Engineering Cookbook
Focuses on authorized Android diagnostics, ADB/Fastboot automation, firmware analysis, VBMeta/SELinux lab inspection, Knox attestation, digital forensics, and safe laboratory methodologies.

### Book Three: Platform Engineering, Automation, and Secure Operations Cookbook
Focuses on backend microservice development, .NET 8 Web APIs, PostgreSQL schema security, Redis caching, API Gateway routing, CI/CD with GitHub Actions, and production DevSecOps runbooks.

---

## 3. Detailed Table of Contents

### Book 1: Foundations, Architecture, and Workstation Cookbook
- **Chapter 1: Platform Overview and Architecture Principles**
  - Recipe 1.1: Environment Prerequisites & Architecture Walkthrough
  - Recipe 1.2: Establishing Ethical Boundaries & Legal Safety Frameworks
- **Chapter 2: Windows & Workstation Engineering Environment**
  - Recipe 2.1: PowerShell 7 and Security Development Configuration
  - Recipe 2.2: Git & GitHub Workflow Setup with SSH Key Hardware Protection
  - Recipe 2.3: Installing .NET 8 SDK, Node.js, and Android Command Line Tools
- **Chapter 3: Containerization & Database Infrastructure**
  - Recipe 3.1: Docker Desktop & WSL2 Backend Setup
  - Recipe 3.2: Deploying PostgreSQL with Least-Privilege Roles via Docker Compose
  - Recipe 3.3: Configuring Redis Cache with Cryptographic Token Stores
- **Chapter 4: Repository Setup & Verification Pipelines**
  - Recipe 4.1: Workspace Cloning & Build Pipeline Restoration
  - Recipe 4.2: Automated Pre-Merge Validation (`validate-premerge.ps1` / `.sh`)
  - Recipe 4.3: Local Smoke Testing & Capturing Evidence Logs

### Book 2: Android Security Research and Device Engineering Cookbook
- **Chapter 5: Android Platform Fundamentals & Architecture**
  - Recipe 5.1: Android Boot Chain & Kernel Partition Mapping
  - Recipe 5.2: Analyzing SELinux Enforcement Domains (`untrusted_app` vs `system_app`)
- **Chapter 6: ADB & Fastboot Automation Frameworks**
  - Recipe 6.1: Automated Device Discovery & Diagnostics via ADB
  - Recipe 6.2: Fastboot Command Operations in Isolated Hardware Labs
  - Recipe 6.3: Automated Package & Permission Auditing
- **Chapter 7: Firmware Analysis & Integrity Verification**
  - Recipe 7.1: Parsing and Auditing `build.prop` Security Flags
  - Recipe 7.2: VBMeta & Verified Boot State Verification
  - Recipe 7.3: Bootkit Detection Heuristics & Ramdisk Inspection in Authorized Labs
- **Chapter 8: Device Trust, Hardware Attestation & Governance**
  - Recipe 8.1: Hardware TEE & Knox Attestation State Analysis
  - Recipe 8.2: Agent Governance Policy Engine Configuration
  - Recipe 8.3: Immutable Telemetry Logging & Forensic Evidence Capture

### Book 3: Platform Engineering, Automation, and Secure Operations Cookbook
- **Chapter 9: .NET 8 Backend Services & API Gateway Architecture**
  - Recipe 9.1: Building the Identity API with ASP.NET Core & JWT Authentication
  - Recipe 9.2: Implementing Device Trust Microservice Endpoints
  - Recipe 9.3: Configuring NGINX API Gateway with TLS Termination & Rate Limiting
- **Chapter 10: PostgreSQL Schema Security & Database Hardening**
  - Recipe 10.1: Designing Row-Level Security (RLS) & Role Separation in PostgreSQL
  - Recipe 10.2: Automated Database Migration & Schema Validation
- **Chapter 11: CI/CD Pipelines, Security Automation & Release Engineering**
  - Recipe 11.1: GitHub Actions CI/CD Pipeline with Automated SAST & Linting
  - Recipe 11.2: Docker Container Validation & SBOM Generation
  - Recipe 11.3: Incident Response Runbooks & Operational Disaster Recovery

---

## 4. Source Files Required for Accurate Drafting
1. Source for any additional custom native/JNI libraries or shell script wrappers.
2. Complete API contracts and OpenAPI specifications for backend services.
3. PostgreSQL migration scripts and database initialization files.
4. Docker Compose deployment manifests for local test clusters.

---

## 5. Known Risks, Gaps, and Assumptions
- **Dual-Use UI Safeguards**: All references to VBMeta, bootkit detection, or SELinux domain transitions are strictly framed as defensive, authorized laboratory recipes for owned devices or emulators.
- **Backend Gap Management**: Features in Book 3 covering .NET microservices and Docker clusters will be clearly marked as *Architectural Specs & Implementation Blueprints* until source code is merged into the root repository.
- **Zero Bypass Policy**: No instructions will be provided for unauthorized exploitation, lock bypass, or third-party device compromise.

---

## 6. Recommended Drafting Sequence & Quality Gates
1. **Stage 1**: Complete detailed file inventory and repository map. *(Complete)*
2. **Stage 2**: Master Series Blueprint & Assessment approval. *(Complete)*
3. **Stage 3**: Chapter-by-chapter drafting of Book 1, starting with Chapter 1.
4. **Stage 4**: Rigorous Quality-Gate review (Syntax, Commands, Verification, Safety).
5. **Stage 5**: Chapter-by-chapter drafting of Book 2 (Android Engineering & Research).
6. **Stage 6**: Chapter-by-chapter drafting of Book 3 (Platform Microservices & Operations).

---

## 7. Recipe Format Standard
Every recipe follows the strict 16-section structure:
`Title` -> `Objective` -> `Why This Matters` -> `Difficulty` -> `Estimated Time` -> `Environment` -> `Prerequisites` -> `Required Files` -> `Security Notice` -> `Procedure` (Windows & Linux) -> `How It Works` -> `Verification` -> `Troubleshooting` -> `Common Mistakes` -> `Security Considerations` -> `Cleanup/Rollback` -> `Evidence to Retain`.
