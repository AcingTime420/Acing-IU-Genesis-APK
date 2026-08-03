package com.example.security

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.util.Log
import org.json.JSONObject
import org.tensorflow.lite.InterpreterApi
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Utility class to load TensorFlow Lite model flatbuffer and vocabulary index JSON maps from assets.
 */
class TfLiteModelLoader(private val context: Context) {

    private val TAG = "TfLiteModelLoader"

    var interpreter: InterpreterApi? = null
        private set

    val wordIndexMap = mutableMapOf<String, Int>()
    val indexWordMap = mutableMapOf<Int, String>()

    init {
        loadVocabularyMaps()
        initInterpreter()
    }

    private fun loadVocabularyMaps() {
        try {
            // Load word_index.json
            context.assets.open("word_index.json").use { inputStream ->
                val reader = InputStreamReader(inputStream, "UTF-8")
                val jsonString = reader.readText()
                val jsonObject = JSONObject(jsonString)
                val keys = jsonObject.keys()
                while (keys.hasNext()) {
                    val word = keys.next()
                    val id = jsonObject.getInt(word)
                    wordIndexMap[word] = id
                }
            }

            // Load index_word.json
            context.assets.open("index_word.json").use { inputStream ->
                val reader = InputStreamReader(inputStream, "UTF-8")
                val jsonString = reader.readText()
                val jsonObject = JSONObject(jsonString)
                val keys = jsonObject.keys()
                while (keys.hasNext()) {
                    val keyStr = keys.next()
                    val id = keyStr.toInt()
                    val word = jsonObject.getString(keyStr)
                    indexWordMap[id] = word
                }
            }
            Log.d(TAG, "Vocabulary maps loaded successfully. Vocab size: ${wordIndexMap.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load vocabulary JSON maps", e)
        }
    }

    private fun initInterpreter() {
        try {
            val modelBuffer = loadModelFile("model.tflite")
            val options = InterpreterApi.Options().apply {
                setNumThreads(2)
            }
            interpreter = InterpreterApi.create(modelBuffer, options)
            Log.d(TAG, "TensorFlow Lite InterpreterApi initialized successfully.")
        } catch (e: Exception) {
            Log.w(TAG, "InterpreterApi fallback: TFLite model buffer parsing safely caught in environment. ${e.message}")
            interpreter = null
        }
    }

    fun loadModelFile(modelFileName: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = context.assets.openFd(modelFileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long = fileDescriptor.startOffset
        val declaredLength: Long = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun tokensToIds(tokens: List<String>): IntArray {
        return tokens.map { token ->
            wordIndexMap[token.lowercase()] ?: wordIndexMap["<OOV>"] ?: 0
        }.toIntArray()
    }
}
