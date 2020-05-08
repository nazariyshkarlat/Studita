package com.example.studita.data

import com.example.studita.data.database.StuditaDatabase

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.studita.data.entity.UserDataEntity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After

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

        val dbUserData = database.getUserDataDao().getUserData()

        MatcherAssert.assertThat(dbUserData, CoreMatchers.equalTo(userData))

        database.getUserDataDao().deleteUserData()

        MatcherAssert.assertThat(dbUserData, CoreMatchers.equalTo(userData))

    }

    @After
    fun cleanup() {
        database.close()
    }
}
