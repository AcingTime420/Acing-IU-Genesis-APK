package com.example.acingiu.sandbox

import java.net.NetworkInterface
import java.util.Collections

class LocalNetworkAuditScript {

    /**
     * Enumerates active network adapters and flags virtual tunneling structures ("tun", "tap", "ppp")
     * that breach sandbox containment profiles.
     */
    fun executeInterfaceAudit(): List<String> {
        val flaggedRisks = mutableListOf<String>()

        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in interfaces) {
                if (networkInterface.isUp) {
                    val adapterName = networkInterface.name.lowercase()
                    val displayName = networkInterface.displayName.lowercase()

                    if (adapterName.contains("tun") || adapterName.contains("tap") || adapterName.contains("ppp") ||
                        displayName.contains("tun") || displayName.contains("tap") || displayName.contains("ppp")
                    ) {
                        flaggedRisks.add(
                            "UNAUTHORIZED_TUNNEL_DETECTED: Interface '${networkInterface.name}' (${networkInterface.displayName}) is active and may bypass sandbox isolation."
                        )
                    }
                }
            }
        } catch (e: Exception) {
            flaggedRisks.add("AUDIT_EXCEPTION: Failed to enumerate network interfaces: ${e.localizedMessage}")
        }

        return flaggedRisks
    }

    /**
     * Verifies whether the current sandbox network environment conforms to security standards.
     */
    fun performEnvironmentSanityCheck(): Boolean {
        return executeInterfaceAudit().isEmpty()
    }
}
