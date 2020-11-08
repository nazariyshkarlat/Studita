package com.studita.data.cache.interesting

interface InterestingCache {
    fun saveInterestingListJson(json: String)

    fun getInterestingJson(interestingNumber: Int): String?

    fun isCached(interestingNumber: Int): Boolean
}