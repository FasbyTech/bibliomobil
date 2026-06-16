package com.fasby.bibliomobil.domain.usecase

import javax.inject.Inject

class CalculateFuzzyMatchUseCase @Inject constructor() {
    
    /**
     * Devuelve un valor entre 0.0 y 1.0 que representa el porcentaje de similitud.
     */
    fun execute(str1: String, str2: String): Double {
        val len1 = str1.length
        val len2 = str2.length
        
        val dp = Array(len1 + 1) { IntArray(len2 + 1) }
        
        for (i in 0..len1) dp[i][0] = i
        for (j in 0..len2) dp[0][j] = j
        
        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (str1[i - 1].lowercaseChar() == str2[j - 1].lowercaseChar()) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,        // Eliminación
                    dp[i][j - 1] + 1,        // Inserción
                    dp[i - 1][j - 1] + cost  // Sustitución
                )
            }
        }
        
        val maxLen = maxOf(len1, len2)
        if (maxLen == 0) return 1.0
        
        return (maxLen - dp[len1][len2]).toDouble() / maxLen.toDouble()
    }
}
