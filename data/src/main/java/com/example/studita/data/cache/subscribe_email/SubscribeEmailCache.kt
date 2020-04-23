package com.example.studita.data.cache.subscribe_email

interface SubscribeEmailCache{
    fun saveSubscribeEmailJson(json: String)

    fun getSubscribeEmailJson(): String?

    fun deleteSubscribeEmailJson()

    fun isCached(): Boolean
}