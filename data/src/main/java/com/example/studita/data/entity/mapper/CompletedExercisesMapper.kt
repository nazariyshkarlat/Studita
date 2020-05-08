package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.CompletedExercisesEntity
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.entity.CompletedExercisesData

class CompletedExercisesMapper : Mapper<CompletedExercisesData, CompletedExercisesEntity>{

    override fun map(source: CompletedExercisesData): CompletedExercisesEntity = CompletedExercisesEntity(source.chapterNumber, source.chapterPartNumber, source.percent, DateTimeFormat().format(source.datetime), source.obtainedTime)

}