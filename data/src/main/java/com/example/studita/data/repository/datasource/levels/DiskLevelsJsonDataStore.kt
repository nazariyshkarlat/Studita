package com.example.studita.data.repository.datasource.levels

import com.example.studita.data.cache.levels.LevelsCache
import com.example.studita.domain.exception.NetworkConnectionException

class DiskLevelsJsonDataStore(
    private val levelsCache: LevelsCache
) : LevelsJsonDataStore {

    override suspend fun getLevelsJson(isLoggedIn: Boolean) = levelsCache.getLevelsJson(isLoggedIn) ?: throw NetworkConnectionException()

}