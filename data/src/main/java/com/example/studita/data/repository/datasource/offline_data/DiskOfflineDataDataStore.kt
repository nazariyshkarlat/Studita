package com.example.studita.data.repository.datasource.offline_data

import com.example.studita.data.cache.chapter.ChaptersCache
import com.example.studita.data.cache.exercises.ExercisesCache
import com.example.studita.data.cache.interesting.InterestingCache
import com.example.studita.data.cache.levels.LevelsCache
import com.example.studita.data.repository.datasource.levels.LevelsJsonDataStore
import com.example.studita.domain.exception.NetworkConnectionException

class DiskOfflineDataDataStore(
    private val levelsCache: LevelsCache,
    private val chaptersCache: ChaptersCache,
    private val exercisesCache: ExercisesCache,
    private val interestingCache: InterestingCache
)  {

    fun offlineDataIsCached() = levelsCache.isCached() && chaptersCache.isCached() && exercisesCache.isCached()

    fun saveOfflineDataJson(levelsJson: String, chaptersJson: String, exercisesJson: String, interestingJson: String) {
        levelsCache.saveLevelsJson(levelsJson)
        chaptersCache.saveChaptersJson(chaptersJson)
        exercisesCache.saveExercisesJson(exercisesJson)
        interestingCache.saveInterestingListJson(interestingJson)
    }

}