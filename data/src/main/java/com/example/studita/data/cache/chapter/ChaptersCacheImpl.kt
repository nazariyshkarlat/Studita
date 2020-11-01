package com.example.studita.data.cache.chapter

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class ChaptersCacheImpl(private val sharedPreferences: SharedPreferences) :
    ChaptersCache {

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

    override fun getChaptersJson(chapterNumber: Int): String? =
        sharedPreferences.getString("${CHAPTER_PREFS}_$chapterNumber", null)

    override fun isCached(): Boolean {
        val value = sharedPreferences.getString("${CHAPTER_PREFS}_${0}", null)
        return value?.isNotEmpty() ?: false
    }

}