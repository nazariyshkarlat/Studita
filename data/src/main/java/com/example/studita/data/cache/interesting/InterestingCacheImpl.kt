package com.example.studita.data.cache.interesting

import android.content.SharedPreferences

class InterestingCacheImpl(private val sharedPreferences: SharedPreferences) : InterestingCache{

    companion object{
        const val INTERESTING_PREFS = "interesting_cache"
    }

    override fun saveInterestingJson(interestingNumber: Int, json: String) {
        sharedPreferences.edit().putString("${INTERESTING_PREFS}_$interestingNumber", json).apply()
    }

    override fun getInterestingJson(interestingNumber: Int): String? = sharedPreferences.getString("${INTERESTING_PREFS}_$interestingNumber", null) ?: null

    override fun isCached(interestingNumber: Int): Boolean {
        val value = sharedPreferences.getString("${INTERESTING_PREFS}_$interestingNumber", null)
        return value?.isNotEmpty() ?: false
    }

}