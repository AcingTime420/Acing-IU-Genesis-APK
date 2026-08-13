package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AcingDatabase
import com.example.logging.FirmwareAuditLogger
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Acing IU: Genesis", appName)
  }

  @Test
  fun `firmware audit logger writes security events to room`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val logger = FirmwareAuditLogger.getInstance(context)

    val corrId = logger.logBiometricAccessAttempt(
      operatorRole = "Principal Architect",
      isSuccess = true,
      authType = "BIOMETRIC_STRONG"
    )
    assertNotNull(corrId)
    assertTrue(corrId.startsWith("BIO-FW-"))

    val db = AcingDatabase.getDatabase(context)
    val logs = db.securityDao().getLogsOlderThan(System.currentTimeMillis() + 10000)
    assertTrue(logs.any { it.correlationId == corrId && it.category == "BIOMETRIC_AUTH" })
  }

  @Test
  fun `security audit database records immutable audit trail`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val auditDb = com.example.data.SecurityAuditDatabase.getDatabase(context)
    val dao = auditDb.securityAuditDao()

    val event = com.example.data.SecurityAuditEventEntity(
      security_level = "CRITICAL",
      message = "Firmware hash mismatch detected during cryptanalysis"
    )
    val eventId = dao.insertAuditEvent(event)
    assertTrue(eventId > 0)

    val recentEvents = dao.getRecentAuditEvents(10)
    assertTrue(recentEvents.any { it.event_id == eventId && it.security_level == "CRITICAL" })
  }
}

