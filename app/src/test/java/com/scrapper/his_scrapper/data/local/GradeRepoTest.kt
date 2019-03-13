package com.scrapper.his_scrapper.data.local

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.scrapper.his_scrapper.application.Grade
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GradeRepoTest {
    val mockGradeDao = mock<GradeDao>()

    fun create() = GradeRepo(mockGradeDao)

    private fun getGrade(name: String = "name1"): Grade = Grade(
        name = name,
        passed = true,
        semester = "some"
    )

    @Test
    fun `should update existing grades`() {
        runBlocking {
            val existingGrade = getGrade()

            whenever(mockGradeDao.getAll()).thenReturn(listOf(existingGrade))

            create().updateOrCreate(listOf(existingGrade))

            verify(mockGradeDao).update(any())
        }
    }

    @Test
    fun `should insert new grades`() {
        runBlocking {
            val existingGrade = getGrade("existing")
            val newGrade = getGrade("new")

            whenever(mockGradeDao.getAll()).thenReturn(listOf(existingGrade))

            create().updateOrCreate(listOf(newGrade))

            verify(mockGradeDao).insert(any())
        }
    }
}