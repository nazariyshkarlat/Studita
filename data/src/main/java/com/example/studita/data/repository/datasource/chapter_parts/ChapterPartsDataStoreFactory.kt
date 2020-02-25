package com.example.studita.data.repository.datasource.chapter_parts

import com.example.studita.data.repository.datasource.exercises.ExercisesDataStore

class ChapterPartsDataStoreFactoryImpl(
    private val chapterPartsDataStore: ChapterPartsDataStore
) : ChapterPartsDataStoreFactory{

    override fun create(priority: ChapterPartsDataStoreFactory.Priority) =
        if (priority == ChapterPartsDataStoreFactory.Priority.CLOUD)
            chapterPartsDataStore
        else
            chapterPartsDataStore
}

interface ChapterPartsDataStoreFactory {

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun create(priority: Priority): ChapterPartsDataStore
}