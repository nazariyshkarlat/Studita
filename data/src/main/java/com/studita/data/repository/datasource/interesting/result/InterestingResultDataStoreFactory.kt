package com.studita.data.repository.datasource.interesting.result

class InterestingResultDataStoreFactoryImpl(
    private val interestingResultDataStoreImpl: InterestingResultDataStoreImpl
) : InterestingResultDataStoreFactory {

    override fun create() =
        interestingResultDataStoreImpl
}

interface InterestingResultDataStoreFactory {

    fun create(): InterestingResultDataStore
}