package com.example.studita.presentation.model.mapper

import com.example.studita.data.entity.mapper.Mapper
import com.example.studita.domain.entity.LevelsDataData

class LevelsDataArrayMapper : Mapper<LevelsDataData, ArrayList<Int>>{
    override fun map(source: LevelsDataData): ArrayList<Int> = arrayListOf(source.chapterOneCompletedCount, source.chapterTwoCompletedCount, source.chapterThreeCompletedCount, source.chapterFourCompletedCount)
}