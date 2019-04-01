package com.scrapper.his_scrapper.data.local

import androidx.room.*
import com.scrapper.his_scrapper.application.Grade
import com.scrapper.his_scrapper.application.asyncIO
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

@Database(entities = [Grade::class], version = 1)
@TypeConverters(Converters::class)
abstract class ScrapperDatabase : RoomDatabase() {
    abstract fun gradeDao(): GradeDao
}


@Dao
interface GradeDao {
    @Query("SELECT * FROM grades")
    fun getAll(): List<Grade>

    @Query("SELECT * FROM grades WHERE uid=:id")
    fun getById(id: Long): Grade

    @Insert
    fun insert(grade: Grade): Long

    @Update
    fun update(grade: Grade)

    @Delete
    fun delete(grade: Grade)
}

interface IGradeRepo {
    suspend fun getAll(): List<Grade>

    suspend fun insert(word: Grade): Long

    suspend fun updateOrCreate(grades: List<Grade>)

    suspend fun getById(id: Long): Grade

    suspend fun update(grade: Grade)

    suspend fun delete(grade: Grade)
}


class GradeRepo @Inject constructor(private val gradeDao: GradeDao) : IGradeRepo {
    override suspend fun updateOrCreate(grades: List<Grade>) = asyncIO {
        val existingByName = getAll().map { it.name to it }.toMap()
        grades.forEach {
            val existingGrade = existingByName.getOrElse(it.name) { null }
            if (existingGrade == null) {
                gradeDao.insert(it)
            } else {
                gradeDao.update(existingGrade)
            }
        }
    }

    override suspend fun update(grade: Grade) = asyncIO {
        gradeDao.update(grade)
    }

    override suspend fun getById(id: Long): Grade = asyncIO {
        gradeDao.getById(id)
    }

    override suspend fun getAll(): List<Grade> = asyncIO {
        gradeDao.getAll()
    }

    override suspend fun insert(word: Grade): Long = asyncIO {
        gradeDao.insert(word)
    }

    override suspend fun delete(grade: Grade) = asyncIO {
        gradeDao.delete(grade)
    }
}

