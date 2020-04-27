package com.example.studita.data.cache.exercises

import android.content.SharedPreferences
import com.example.studita.data.cache.chapter.ChapterCacheImpl
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

class ExercisesCacheImpl(private val sharedPreferences: SharedPreferences) : ExercisesCache{

    companion object{
        const val EXERCISES_PREFS = "exercises_cache"
    }

    override fun saveExercisesJson(json: String) {
        val chapterParts = Gson().fromJson<List<JsonObject>>(json, object : TypeToken<List<JsonObject>>() {}.type)
        chapterParts.forEachIndexed {chapterPartNumber, chapterPart->
            sharedPreferences.edit().putString("${EXERCISES_PREFS}_${chapterPartNumber+1}", chapterPart.toString()).apply()
        }
    }

    override fun getExercises(chapterPartNumber: Int): String? {
        println(chapterPartNumber)
        return sharedPreferences.getString("${EXERCISES_PREFS}_$chapterPartNumber", null) ?: null
    }

    override fun isCached(): Boolean {
        val value = sharedPreferences.getString("${EXERCISES_PREFS}_${1}", null)
        return value?.isNotEmpty() ?: false
    }

}