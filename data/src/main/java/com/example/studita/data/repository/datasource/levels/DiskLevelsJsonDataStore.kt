package com.example.studita.data.repository.datasource.levels

import com.example.studita.data.database.levels.LevelsCache
import com.example.studita.domain.exception.NetworkConnectionException

class DiskLevelsJsonDataStore(
    private val levelsCache: LevelsCache
) : LevelsJsonDataStore {

    override suspend fun getLevelsJson() = levelsCache.getLevelsJson() ?: throw NetworkConnectionException()

}