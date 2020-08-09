package com.example.studita.data.cache.subscribe_email

import android.content.SharedPreferences

class SubscribeEmailCacheImpl(private val sharedPreferences: SharedPreferences) :
    SubscribeEmailCache {

    companion object {
        const val SUBSCRIBE_EMAIL_PREFS = "subscribe_email_cache"
    }

    override fun saveSubscribeEmailJson(json: String) {
        sharedPreferences.edit().putString(SUBSCRIBE_EMAIL_PREFS, json).apply()
    }

    override fun getSubscribeEmailJson(): String? =
        sharedPreferences.getString(SUBSCRIBE_EMAIL_PREFS, null)

    override fun deleteSubscribeEmailJson() {
        sharedPreferences.edit().remove(SUBSCRIBE_EMAIL_PREFS).apply()
    }

    override fun isCached(): Boolean {
        val value = sharedPreferences.getString(SUBSCRIBE_EMAIL_PREFS, null)
        return value?.isNotEmpty() ?: false
    }
}