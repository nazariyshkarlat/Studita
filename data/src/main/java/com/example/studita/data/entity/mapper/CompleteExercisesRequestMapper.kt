package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.CompleteExercisesRequest
import com.example.studita.domain.entity.CompleteExercisesRequestData

class CompleteExercisesRequestMapper(private val userIdTokenMapper: UserIdTokenMapper, private val completedExercisesMapper: CompletedExercisesMapper) : Mapper<CompleteExercisesRequestData, CompleteExercisesRequest>{

    override fun map(source: CompleteExercisesRequestData): CompleteExercisesRequest = CompleteExercisesRequest(userIdTokenMapper.map(source.userIdToken!!), completedExercisesMapper.map(source.completedExercisesEntity))

}