package com.studita.data.cache.chapter

import android.content.SharedPreferences
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class ChaptersCacheImpl(private val sharedPreferences: SharedPreferences) :
    ChaptersCache {

    companion object {
        const val CHAPTER_PREFS = "chapter_cache"
    }

    override fun saveChaptersJson(json: String) {
        val chapters =
            Json.decodeFromString<List<JsonObject>>(json)
        chapters.forEachIndexed { index, chapter ->
            sharedPreferences.edit().putString("${CHAPTER_PREFS}_${index + 1}", chapter.toString())
                .apply()
        }
    }

    override fun getChaptersJson(chapterNumber: Int): String? =
        sharedPreferences.getString("${CHAPTER_PREFS}_$chapterNumber", null)

    override fun isCached(): Boolean {
        val value = sharedPreferences.getString("${CHAPTER_PREFS}_${0}", null)
        return value?.isNotEmpty() ?: false
    }

}