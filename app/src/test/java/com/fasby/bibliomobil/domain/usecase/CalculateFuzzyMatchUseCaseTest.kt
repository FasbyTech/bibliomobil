package com.fasby.bibliomobil.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateFuzzyMatchUseCaseTest {

    private lateinit var useCase: CalculateFuzzyMatchUseCase

    @Before
    fun setUp() {
        useCase = CalculateFuzzyMatchUseCase()
    }

    @Test
    fun `execute returns 1_0 for identical strings`() {
        val result = useCase.execute("Batman", "Batman")
        assertEquals(1.0, result, 0.001)
    }

    @Test
    fun `execute is case insensitive`() {
        val result = useCase.execute("batman", "BATMAN")
        assertEquals(1.0, result, 0.001)
    }

    @Test
    fun `execute returns 0_0 for completely different strings`() {
        val result = useCase.execute("abc", "def")
        assertEquals(0.0, result, 0.001)
    }

    @Test
    fun `execute handles partial matches correctly`() {
        // "Batma" vs "Batman" -> 1 deletion/insertion away. maxLen = 6. (6-1)/6 = 0.833
        val result = useCase.execute("Batma", "Batman")
        assertEquals(0.833, result, 0.001)
    }

    @Test
    fun `execute handles empty strings`() {
        assertEquals(0.0, useCase.execute("", "something"), 0.0)
        assertEquals(0.0, useCase.execute("something", ""), 0.0)
        assertEquals(1.0, useCase.execute("", ""), 0.0)
    }
}
