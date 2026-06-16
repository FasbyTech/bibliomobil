package com.fasby.bibliomobil.domain.usecase

import javax.inject.Inject

class CalculateFuzzyMatchUseCase @Inject constructor() {
    
    /**
     * Devuelve un valor entre 0.0 y 1.0 que representa el porcentaje de similitud.
     */
    fun execute(str1: String, str2: String): Double {
        if (str1 == str2) return 1.0
        if (str1.isEmpty() || str2.isEmpty()) return 0.0

        val s1 = str1.lowercase()
        val s2 = str2.lowercase()
        val len1 = s1.length
        val len2 = s2.length

        // Optimizamos el espacio usando solo dos filas
        var prevRow = IntArray(len2 + 1) { it }
        var currentRow = IntArray(len2 + 1)

        for (i in 1..len1) {
            currentRow[0] = i
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                currentRow[j] = minOf(
                    prevRow[j] + 1,
                    currentRow[j - 1] + 1,
                    prevRow[j - 1] + cost
                )
            }
            // Swap rows
            val temp = prevRow
            prevRow = currentRow
            currentRow = temp
        }

        val distance = prevRow[len2]
        val maxLen = maxOf(len1, len2)
        return (maxLen - distance).toDouble() / maxLen.toDouble()
    }
}
