package com.example.studita.data.database.chapter_parts

import android.content.Context
import android.content.SharedPreferences

class ChapterCacheImpl(private val sharedPreferences: SharedPreferences) :
    ChapterCache {

    companion object{
        const val CHAPTER_PREFS = "chapter_cache"
    }

    override fun putLevelsJson(chapterNumber: Int, json: String) {
        sharedPreferences.edit().putString("${CHAPTER_PREFS}_$chapterNumber", json).apply()
    }

    override fun getLevelsJson(chapterNumber: Int): String? = sharedPreferences.getString("${CHAPTER_PREFS}_$chapterNumber", null) ?: null

    override fun isCached(chapterNumber: Int): Boolean {
        val value = sharedPreferences.getString("${CHAPTER_PREFS}_$chapterNumber", null)
        return value?.isNotEmpty() ?: false
    }

}