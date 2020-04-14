package com.example.studita.data.cache.interesting

interface InterestingCache{
    fun saveInterestingJson(interestingNumber: Int, json: String)

    fun getInterestingJson(interestingNumber: Int): String?

    fun isCached(interestingNumber: Int): Boolean
}