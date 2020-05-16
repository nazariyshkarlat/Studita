package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.CompleteExercisesRequest
import com.example.studita.data.entity.CompletedExercisesEntity
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.entity.CompleteExercisesRequestData
import com.example.studita.domain.entity.CompletedExercisesData

class CompleteExercisesRequestMapper(private val userIdTokenMapper: UserIdTokenMapper, private val completedExercisesMapper: CompletedExercisesMapper) : Mapper<CompleteExercisesRequestData, CompleteExercisesRequest>{

    override fun map(source: CompleteExercisesRequestData): CompleteExercisesRequest = CompleteExercisesRequest(userIdTokenMapper.map(source.userIdToken!!), completedExercisesMapper.map(source.completedExercisesEntity))

}

class CompletedExercisesMapper : Mapper<CompletedExercisesData, CompletedExercisesEntity>{

    override fun map(source: CompletedExercisesData): CompletedExercisesEntity = CompletedExercisesEntity(source.chapterNumber, source.chapterPartNumber, source.percent, DateTimeFormat().format(source.datetime), source.obtainedTime)

}