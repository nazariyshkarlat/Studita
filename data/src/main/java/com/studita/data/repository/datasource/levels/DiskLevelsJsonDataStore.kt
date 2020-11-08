package com.studita.data.repository.datasource.levels

import com.studita.data.cache.levels.LevelsCache
import com.studita.domain.exception.NetworkConnectionException

class DiskLevelsJsonDataStore(
    private val levelsCache: LevelsCache
) : LevelsJsonDataStore {

    override suspend fun getLevelsJson() =
        levelsCache.getLevelsJson() ?: throw NetworkConnectionException()

    fun levelsAreCached() = levelsCache.isCached()

    fun saveLevelsJson(json: String) =
        levelsCache.saveLevelsJson(json)
}