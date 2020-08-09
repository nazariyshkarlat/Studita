package com.example.studita.data.cache.chapter

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class ChapterCacheImpl(private val sharedPreferences: SharedPreferences) :
    ChapterCache {

    companion object {
        const val CHAPTER_PREFS = "chapter_cache"
    }

    override fun saveChaptersJson(json: String) {
        val chapters =
            Gson().fromJson<List<JsonObject>>(json, object : TypeToken<List<JsonObject>>() {}.type)
        chapters.forEachIndexed { index, chapter ->
            sharedPreferences.edit().putString("${CHAPTER_PREFS}_${index + 1}", chapter.toString())
                .apply()
        }
    }

    override fun getLevelsJson(chapterNumber: Int): String? =
        sharedPreferences.getString("${CHAPTER_PREFS}_$chapterNumber", null)

    override fun isCached(chapterNumber: Int): Boolean {
        val value = sharedPreferences.getString("${CHAPTER_PREFS}_$chapterNumber", null)
        return value?.isNotEmpty() ?: false
    }

}