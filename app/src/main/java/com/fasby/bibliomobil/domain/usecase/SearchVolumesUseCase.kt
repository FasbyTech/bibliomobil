package com.fasby.bibliomobil.domain.usecase

import com.fasby.bibliomobil.data.local.model.DetailedVolume
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchVolumesUseCase @Inject constructor(
    private val repository: VolumeRepository,
    private val calculateFuzzyMatchUseCase: CalculateFuzzyMatchUseCase
) {

    /**
     * Busca volúmenes y los ordena por similitud con el texto extraído por OCR.
     */
    fun executeOcrFuzzyMatch(ocrRawText: String): Flow<List<Pair<DetailedVolume, Double>>> {
        return repository.getAllVolumes().map { volumes ->
            volumes.map { volume ->
                val score = calculateFuzzyMatchUseCase.execute(ocrRawText, volume.volume.title)
                volume to score
            }.filter { it.second > 0.3 } // Filtro de confianza mínimo
                .sortedByDescending { it.second }
        }
    }
    
    fun executeTextSearch(query: String): Flow<List<DetailedVolume>> {
        return repository.searchVolumes(query)
    }
}
