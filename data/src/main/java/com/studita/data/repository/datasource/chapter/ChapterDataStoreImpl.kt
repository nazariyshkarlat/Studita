package com.studita.data.repository.datasource.chapter

import com.studita.data.entity.ChapterEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ChapterDataStoreImpl(private val chapterJsonDataStore: ChapterJsonDataStore) :
    ChapterDataStore {

    private val type: Type = object : TypeToken<ChapterEntity>() {}.type

    override suspend fun getChapterEntity(chapterNumber: Int): Pair<Int, ChapterEntity> {
        val pair = chapterJsonDataStore.getChapterJson(chapterNumber)
        return pair.first to Gson().fromJson<ChapterEntity>(pair.second, type)
    }

}