package com.example.security

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.Locale

/**
 * Data model for Next-Word Prediction results.
 */
data class PredictionResult(
    val predictedWord: String,
    val confidence: Float,
    val contextTokens: List<String>,
    val topCandidates: List<Pair<String, Float>>
)

/**
 * On-device Predictive Model Engine for Next-Word Auto-Typing.
 * Supports vocabulary lookup, N-gram context evaluation, and confidence scoring.
 */
class PredictiveKeyboardModelEngine(
    val vocabSize: Int = 2000,
    val contextWindowSize: Int = 5
) {
    private val wordToIndex = mutableMapOf<String, Int>()
    private val indexToWord = mutableMapOf<Int, String>()

    // Core n-gram probability dictionary
    private val nGramDictionary = mapOf(
        "how" to listOf("are" to 0.92f, "is" to 0.05f, "do" to 0.03f),
        "are" to listOf("you" to 0.88f, "we" to 0.08f, "they" to 0.04f),
        "you" to listOf("doing" to 0.45f, "today" to 0.35f, "have" to 0.20f),
        "i" to listOf("am" to 0.65f, "will" to 0.20f, "have" to 0.15f),
        "thank" to listOf("you" to 0.95f, "everyone" to 0.05f),
        "good" to listOf("morning" to 0.50f, "afternoon" to 0.30f, "night" to 0.20f),
        "acing" to listOf("matrix" to 0.85f, "security" to 0.12f, "iu" to 0.03f),
        "security" to listOf("policy" to 0.40f, "audit" to 0.35f, "consensus" to 0.25f),
        "what" to listOf("is" to 0.70f, "are" to 0.20f, "happened" to 0.10f),
        "is" to listOf("the" to 0.40f, "your" to 0.30f, "a" to 0.20f)
    )

    init {
        buildVocabulary()
    }

    private fun buildVocabulary() {
        wordToIndex["<OOV>"] = 0
        indexToWord[0] = "<OOV>"
        var id = 1

        nGramDictionary.keys.forEach { word ->
            if (!wordToIndex.containsKey(word)) {
                wordToIndex[word] = id
                indexToWord[id] = word
                id++
            }
        }
        nGramDictionary.values.flatten().forEach { (word, _) ->
            if (!wordToIndex.containsKey(word)) {
                wordToIndex[word] = id
                indexToWord[id] = word
                id++
            }
        }
    }

    fun tokensToIds(tokens: List<String>): IntArray {
        return tokens.map { word -> wordToIndex[word.lowercase(Locale.US)] ?: 0 }.toIntArray()
    }

    fun predictNextWord(inputText: String): PredictionResult {
        val tokens = inputText.trim()
            .split("\\s+".toRegex())
            .filter { it.isNotEmpty() }

        if (tokens.isEmpty()) {
            return PredictionResult("", 0f, emptyList(), emptyList())
        }

        val contextTokens = if (tokens.size >= contextWindowSize) tokens.takeLast(contextWindowSize) else tokens
        val lastToken = contextTokens.last().lowercase(Locale.US)

        val candidates = nGramDictionary[lastToken] ?: listOf("the" to 0.25f, "and" to 0.20f, "to" to 0.15f)
        val sorted = candidates.sortedByDescending { it.second }
        val topMatch = sorted.firstOrNull() ?: ("" to 0f)

        return PredictionResult(
            predictedWord = topMatch.first,
            confidence = topMatch.second,
            contextTokens = contextTokens,
            topCandidates = sorted
        )
    }
}

/**
 * Android Accessibility Service listening for text field input changes,
 * predicting the next word, and performing auto-typing injection.
 */
class AutoKeyboardService : AccessibilityService() {

    private lateinit var modelLoader: TfLiteModelLoader
    private val modelEngine = PredictiveKeyboardModelEngine()

    override fun onServiceConnected() {
        super.onServiceConnected()
        try {
            modelLoader = TfLiteModelLoader(applicationContext)
            Log.d("AutoKeyboardService", "TFLite model loader initialized with ${modelLoader.wordIndexMap.size} vocab entries")
        } catch (e: Exception) {
            Log.e("AutoKeyboardService", "Error initializing TfLiteModelLoader", e)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) return

        val text = event.text?.joinToString("")?.trim() ?: return
        if (text.isEmpty()) return

        val prediction = modelEngine.predictNextWord(text)
        if (prediction.predictedWord.isNotEmpty() && prediction.confidence >= 0.3f) {
            commitPredictedText(prediction.predictedWord)
        }
    }

    override fun onInterrupt() {
        Log.d("AutoKeyboardService", "Service interrupted")
    }

    private fun commitPredictedText(word: String) {
        val rootNode = rootInActiveWindow ?: return
        val editableNode = findEditableNode(rootNode)

        if (editableNode != null) {
            val args = Bundle().apply {
                putCharSequence("ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE", word + " ")
            }
            editableNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        }
    }

    private fun findEditableNode(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (node.isFocused && node.isEditable) return node
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val result = findEditableNode(child)
            if (result != null) return result
        }
        return null
    }
}
