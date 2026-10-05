package com.example.service

import android.util.Log
import com.example.BuildConfig
import com.example.data.TechNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class FeynmanEvaluation(
    val isPassed: Boolean,
    val feedback: String,
    val socraticQuestion: String? = null
)

object AIBrainService {
    private const val TAG = "AIBrainService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun evaluateFeynmanExplanation(
        node: TechNode,
        userExplanation: String
    ): FeynmanEvaluation = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "No valid Gemini API key found. Falling back to offline first-principles evaluation.")
            return@withContext offlineEvaluation(node, userExplanation)
        }

        val systemPrompt = """
            Eres un Tutor Socrático riguroso y profesor de física y química de primeros principios (Feynman Technique).
            El usuario está intentando demostrar maestría sobre el siguiente concepto científico: "${node.title}".
            Principio científico real: ${node.scientificPrinciple}
            Fórmulas/Ecuaciones: ${node.formulaOrEquation}
            
            Evalúa la explicación del usuario:
            - ¿Explica el mecanismo real de causa-efecto a nivel atómico, térmico o mecánico?
            - ¿Evita la memorización ciega y la jerga vacía?
            - ¿Distingue conceptos fundamentales (ej. temperatura vs calor, tensión vs deformación, reducción vs oxidación)?
            
            DEBES responder ÚNICAMENTE con un objeto JSON sin formato markdown con este esquema exacto:
            {
              "isPassed": true o false,
              "feedback": "Análisis conciso y constructivo destacando aciertos o señalando lagunas conceptuales",
              "socraticQuestion": "Si no pasó, UNA sola pregunta socrática que obligue a pensar en la causa raíz; si pasó, dejar vacío o null"
            }
        """.trimIndent()

        val jsonPayload = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Explicación del estudiante sobre ${node.title}:\n\n$userExplanation")
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemPrompt)
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.2)
            })
        }

        val requestUrl = "$BASE_URL?key=$apiKey"
        val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(requestUrl)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseString = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API call failed: ${response.code} $responseString")
                return@withContext offlineEvaluation(node, userExplanation)
            }

            val rootJson = JSONObject(responseString)
            val candidates = rootJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text").orEmpty()

            parseEvaluationJson(text, node, userExplanation)
        } catch (e: Exception) {
            Log.e(TAG, "Error evaluating with Gemini API", e)
            offlineEvaluation(node, userExplanation)
        }
    }

    private fun parseEvaluationJson(jsonText: String, node: TechNode, userExplanation: String): FeynmanEvaluation {
        return try {
            val clean = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            val parsed = JSONObject(clean)
            FeynmanEvaluation(
                isPassed = parsed.optBoolean("isPassed", false),
                feedback = parsed.optString("feedback", "Evaluación completada."),
                socraticQuestion = parsed.optString("socraticQuestion").takeIf { it.isNotBlank() }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse json response: $jsonText", e)
            offlineEvaluation(node, userExplanation)
        }
    }

    private fun offlineEvaluation(node: TechNode, explanation: String): FeynmanEvaluation {
        val trimmed = explanation.trim()
        val wordCount = trimmed.split("\\s+".toRegex()).size

        if (wordCount < 12) {
            return FeynmanEvaluation(
                isPassed = false,
                feedback = "Tu explicación es demasiado breve para evaluar la comprensión real.",
                socraticQuestion = "¿Qué transformaciones físicas o químicas exactas ocurren paso a paso para que ${node.title} funcione?"
            )
        }

        // Check for relevant scientific keywords
        val keywords = when (node.id) {
            "fire" -> listOf("oxígeno", "combustible", "calor", "reacción", "energía", "activación", "celulosa", "exotérmica")
            "clay" -> listOf("silicato", "lámina", "agua", "alúmina", "plasticidad", "filosilicato", "partícula", "adsorbida")
            "charcoal" -> listOf("pirólisis", "oxígeno", "anóxica", "carbono", "volátiles", "calor", "madera", "descomposición")
            "mud_kiln" -> listOf("temperatura", "vitrificación", "poros", "sílice", "fase vítrea", "impermeable", "horno", "calor")
            "copper_smelting" -> listOf("reducción", "monóxido", "carbono", "cobre", "fusión", "oxígeno", "mineral", "1085")
            "bellows_forge" -> listOf("aire", "oxígeno", "flujo", "combustión", "tiro", "presión", "temperatura", "forzado")
            "bronze_alloy" -> listOf("estaño", "cobre", "aleación", "dislocaciones", "dureza", "cristalina", "átomos", "deformación")
            "distillation" -> listOf("vapor", "ebullición", "volátil", "condensación", "calor latente", "separación", "líquido")
            "voltaic_pile" -> listOf("electrolito", "potencial", "ánodo", "cátodo", "electrones", "zinc", "cobre", "redox")
            "faraday_induction" -> listOf("flujo", "magnético", "inducción", "corriente", "bobina", "campo", "variación", "fem")
            "astrolabe" -> listOf("ángulo", "proyección", "latitud", "estrella", "horizonte", "cálculo", "esfera", "triangulación")
            "penicillin" -> listOf("bacteria", "pared", "enzima", "transpeptidasa", "síntesis", "hongo", "antibiótico", "peptidoglicano")
            else -> listOf("energía", "materia", "reacción", "fuerza", "estructura")
        }

        val matches = keywords.count { kw -> trimmed.contains(kw, ignoreCase = true) }

        return if (matches >= 2 && wordCount >= 18) {
            FeynmanEvaluation(
                isPassed = true,
                feedback = "¡Excelente explicación desde primeros principios! Has conectado la causa física microscópica con el resultado observable en ${node.title}.",
                socraticQuestion = null
            )
        } else {
            FeynmanEvaluation(
                isPassed = false,
                feedback = "Vas por buen camino, pero tu explicación aún se apoya en descripciones superficiales sin aclarar el principio fundamental.",
                socraticQuestion = "¿Cómo explicarías lo que sucede a nivel de partículas o energía sin usar el nombre de la tecnología?"
            )
        }
    }
}
