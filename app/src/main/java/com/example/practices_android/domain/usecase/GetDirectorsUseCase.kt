package com.example.practices_android.domain.usecase

import com.example.practices_android.domain.repository.FilmsRepository
import kotlinx.coroutines.flow.Flow

class GetDirectorsUseCase(private val repository: FilmsRepository) {
    operator fun invoke(): Flow<List<String>> = repository.getDirectors()
}
