package com.example.studita.data

import com.example.studita.data.database.StuditaDatabase

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserStatisticsEntity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.AfterClass

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Before

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var database: StuditaDatabase

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StuditaDatabase::class.java
        ).build()
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertAndSelectUserData() = runBlocking {
        val userData = UserDataEntity("inasar0327",
            "23c1c59a-81b7-11ea-b95d-00d8612be2bb",
            "https://lh3.googleusercontent.com/a-/AOh14GjjVfisFrm6D0EV6SooFKf9vZZkxUB3loY6X8Ky=s96-c",
            1,
            0,
            0,
            false,
            arrayListOf(0,0,0,0),
            "2020-04-17 13:23:19")

        database.getUserDataDao().insertUserData(userData)

        val dbUserData = database.getUserDataDao().getUserDataAsync()

        MatcherAssert.assertThat(dbUserData, CoreMatchers.equalTo(userData))

        database.getUserDataDao().deleteUserData()

        MatcherAssert.assertThat(dbUserData, CoreMatchers.equalTo(userData))

    }

    @Test
    @Throws(InterruptedException::class)
    fun insertAndSelectUserStatistics() = runBlocking {
        val userTodayStatistics = UserStatisticsEntity("today", 100, 20, 30, 40, 50)
        val userYesterdayStatistics = UserStatisticsEntity("yesterday", 100, 0, 30, 40, 50)
        val userWeekStatistics = UserStatisticsEntity("week", 100, 20, 30, 4, 50)
        val userMontStatistics = UserStatisticsEntity("month", 100, 6, 30, 40, 50)

        database.getUserStatisticsDao().insertUserStatistics(userTodayStatistics)
        val dbTodayUserStatistics = database.getUserStatisticsDao().getUserStatistics("today")
        MatcherAssert.assertThat(dbTodayUserStatistics, CoreMatchers.equalTo(userTodayStatistics))

        database.getUserStatisticsDao().insertUserStatistics(userYesterdayStatistics)
        val dbYesterdayUserStatistics = database.getUserStatisticsDao().getUserStatistics("yesterday")
        MatcherAssert.assertThat(dbYesterdayUserStatistics, CoreMatchers.equalTo(userYesterdayStatistics))

        database.getUserStatisticsDao().insertUserStatistics(userWeekStatistics)
        val dbWeekUserStatistics = database.getUserStatisticsDao().getUserStatistics("week")
        MatcherAssert.assertThat(dbWeekUserStatistics, CoreMatchers.equalTo(userWeekStatistics))

        database.getUserStatisticsDao().insertUserStatistics(userMontStatistics)
        val dbMonthUserStatistics = database.getUserStatisticsDao().getUserStatistics("month")
        MatcherAssert.assertThat(dbMonthUserStatistics, CoreMatchers.equalTo(userMontStatistics))
    }

    @After
    fun cleanup() {
        database.close()
    }
}
