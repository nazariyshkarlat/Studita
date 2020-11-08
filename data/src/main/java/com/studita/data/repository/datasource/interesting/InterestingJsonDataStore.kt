package com.studita.data.repository.datasource.interesting

interface InterestingJsonDataStore {

    suspend fun getInterestingJson(interestingNumber: Int): Pair<Int, String>

}