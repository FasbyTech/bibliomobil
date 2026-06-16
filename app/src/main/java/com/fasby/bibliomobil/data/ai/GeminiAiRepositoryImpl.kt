package com.fasby.bibliomobil.data.ai

import com.fasby.bibliomobil.BuildConfig
import com.fasby.bibliomobil.domain.ai.AiRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiAiRepositoryImpl @Inject constructor() : AiRepository {

    // Configuración de Seguridad (Safety Settings)
    private val safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
    )

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        safetySettings = safetySettings
    )

    override suspend fun generateSummary(title: String, synopsis: String): String? {
        return try {
            val response = generativeModel.generateContent(
                content {
                    text("Genera un resumen corto y atractivo en español para el siguiente libro/cómic: $title. Sinopsis actual: $synopsis")
                }
            )
            response.text
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun categorizeBook(title: String, synopsis: String): String? {
        return try {
            val response = generativeModel.generateContent(
                content {
                    text("Basándote en el título '$title' y la sinopsis '$synopsis', ¿cuál es el género principal? Responde solo con una palabra (ej: Shonen, Seinen, Terror, Ciencia Ficción).")
                }
            )
            response.text?.trim()
        } catch (e: Exception) {
            null
        }
    }
}
