package com.scrapper.his_scrapper

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.scrapper.his_scrapper.application.Grade
import com.scrapper.his_scrapper.data.local.ScrapperDatabase
import kotlinx.coroutines.*
import org.amshove.kluent.shouldEqual
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class GradleRepoTest {
    @Test
    fun androidRoomUpdateTest() {
        val db = getDbContext()

        val id = db.gradeDao().insert(
            Grade(
                grade = 1.0f,
                name = "some",
                passed = true,
                semester = "some"
            )
        )

        val grade = db.gradeDao().getById(id)
        grade.grade = 3.0f
        db.gradeDao().update(grade)


        var result =  db.gradeDao().getById(id)
        result.grade.shouldEqual(3.0f)
    }

    private fun getDbContext(): ScrapperDatabase {
        val appContext = InstrumentationRegistry.getTargetContext()

        val db = Room.databaseBuilder(
            appContext.applicationContext,
            ScrapperDatabase::class.java,
            "Scrapper_database"
        ).build()
        return db
    }
}