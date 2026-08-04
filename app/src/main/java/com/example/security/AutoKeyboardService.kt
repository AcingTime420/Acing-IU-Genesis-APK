package com.example.security

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * AccessibilityService listening for TYPE_VIEW_TEXT_CHANGED events,
 * loading TFLite next-word prediction model via TfLiteModelLoader / PredictiveKeyboardModelEngine,
 * and injecting predicted text into active text fields.
 */
class AutoKeyboardService : AccessibilityService() {

    private val TAG = "AutoKeyboardService"
    private lateinit var modelLoader: TfLiteModelLoader
    private val modelEngine = PredictiveKeyboardModelEngine()

    override fun onServiceConnected() {
        super.onServiceConnected()
        try {
            modelLoader = TfLiteModelLoader(applicationContext)
            Log.d(TAG, "AutoKeyboardService connected. Loaded TFLite Model with ${modelLoader.wordIndexMap.size} vocabulary entries.")
            TestModeManager.logAction(TAG, "AutoKeyboardService connected & TFLite ModelLoader initialized", isLiveExecuted = true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize TfLiteModelLoader in AutoKeyboardService", e)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) return

        val text = event.text?.joinToString("")?.trim() ?: return
        if (text.isEmpty()) return

        Log.d(TAG, "Captured TYPE_VIEW_TEXT_CHANGED event: '$text'")

        val prediction = modelEngine.predictNextWord(text)
        if (prediction.predictedWord.isNotEmpty() && prediction.confidence >= 0.25f) {
            Log.d(TAG, "Inference predicted next word: '${prediction.predictedWord}' (Confidence: ${prediction.confidence})")
            commitPredictedText(text + " " + prediction.predictedWord)
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "AutoKeyboardService interrupted by system")
    }

    private fun commitPredictedText(fullTextWithPrediction: String) {
        if (TestModeManager.isSimulationMode) {
            TestModeManager.logAction(
                TAG,
                "Simulated auto-typing injection: '$fullTextWithPrediction'",
                isLiveExecuted = false
            )
            return
        }

        val rootNode = rootInActiveWindow ?: return
        val editableNode = findEditableNode(rootNode)

        if (editableNode != null) {
            val args = Bundle().apply {
                putCharSequence("ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE", fullTextWithPrediction)
            }
            val success = editableNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
            TestModeManager.logAction(
                TAG,
                "Injected predicted text into input field via AccessibilityNodeInfo (Success=$success)",
                isLiveExecuted = true
            )
        } else {
            Log.w(TAG, "No editable focused node found for auto-keyboard injection.")
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
