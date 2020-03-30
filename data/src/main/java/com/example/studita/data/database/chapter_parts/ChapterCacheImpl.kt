package com.example.studita.data.database.chapter_parts

import android.content.Context
import android.content.SharedPreferences

class ChapterCacheImpl(private val sharedPreferences: SharedPreferences) :
    ChapterCache {

    override fun putLevelsJson(chapterNumber: Int, json: String) {
        sharedPreferences.edit().putString("chapter_parts_cache_$chapterNumber", json).apply()
    }

    override fun getLevelsJson(chapterNumber: Int): String? = sharedPreferences.getString("chapter_parts_cache_$chapterNumber", null) ?: null

    override fun isCached(chapterNumber: Int): Boolean {
        val value = sharedPreferences.getString("chapter_parts_cache_$chapterNumber", null)
        return value?.isNotEmpty() ?: false
    }

}