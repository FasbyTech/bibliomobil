package com.fasby.bibliomobil.domain.ai

interface AiRepository {
    suspend fun generateSummary(title: String, synopsis: String): String?
    suspend fun categorizeBook(title: String, synopsis: String): String?
}
