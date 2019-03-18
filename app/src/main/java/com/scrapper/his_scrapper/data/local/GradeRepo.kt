package com.scrapper.his_scrapper.data.local

import androidx.room.*
import com.scrapper.his_scrapper.application.Grade
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
    suspend fun getAll(): List<Grade>

    @Insert
    suspend fun insert(grade: Grade)

    @Update
    suspend fun update(grade: Grade)

    @Delete
    suspend fun delete(grade: Grade)
}

interface IGradeRepo {
    suspend fun getAll(): List<Grade>

    suspend fun insert(word: Grade)

    suspend fun updateOrCreate(grades: List<Grade>)
}

class GradeRepo @Inject constructor(private val gradeDao: GradeDao) : IGradeRepo {
    override suspend fun updateOrCreate(grades: List<Grade>) {
        val existingByName = getAll().map { it.name to it }.toMap()
        return grades.forEach {
            val existingGrade = existingByName.getOrElse(it.name) { null }
            if (existingGrade == null) {
                gradeDao.insert(it)
            } else {
                gradeDao.update(existingGrade)
            }
        }
    }

    override suspend fun getAll(): List<Grade> = gradeDao.getAll()

    override suspend fun insert(word: Grade) = gradeDao.insert(word)
}

