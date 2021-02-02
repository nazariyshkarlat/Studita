package com.studita.data.repository.datasource.offline_data

import com.studita.data.cache.chapter.ChaptersCache
import com.studita.data.cache.levels.LevelsCache

class DiskOfflineDataDataStore(
    private val levelsCache: LevelsCache,
    private val chaptersCache: ChaptersCache
)  {

    fun offlineDataIsCached() = levelsCache.isCached() && chaptersCache.isCached()

    fun saveOfflineDataJson(levelsJson: String, chaptersJson: String) {
        levelsCache.saveLevelsJson(levelsJson)
        chaptersCache.saveChaptersJson(chaptersJson)
    }

}