package com.example.ai

import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ThreatAnalysisService(private val geminiService: GeminiService) {
    suspend fun analyzeThreat(file: File): String = withContext(Dispatchers.IO) {
        val content = if (file.exists()) {
            file.readText().take(5000)
        } else {
            "File not found or empty."
        }
        
        val prompt = """
            Analyze the following security log or firmware metadata and provide a summarized threat analysis report.
            Include severity level, potential vulnerabilities, and recommended actions.
            
            Content:
            $content
        """.trimIndent()
        
        try {
            val systemPrompt = "You are an expert Threat Intelligence Analyst."
            val result = geminiService.generateContent(
                prompt = prompt,
                systemPrompt = systemPrompt,
                fallbackGenerator = { "Analysis generated from fallback. Original error: $it" }
            )
            result
        } catch (e: Exception) {
            "Error analyzing threat: ${e.localizedMessage}"
        }
    }
}
