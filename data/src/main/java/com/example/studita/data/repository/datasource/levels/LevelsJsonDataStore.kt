package com.example.studita.data.repository.datasource.levels

interface LevelsJsonDataStore {

    suspend fun getLevelsJson(): String

}