package com.scrapper.his_scrapper.data.local

import androidx.room.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Entity(tableName = "grades")
data class Grade(
    @PrimaryKey(autoGenerate = true) var uid: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "semester") val semester: String,
    @ColumnInfo(name = "is_passed") val passed: Boolean,
    @ColumnInfo(name = "credits") val credits: Float?,
    @ColumnInfo(name = "grade") val grade: Float?,
    @ColumnInfo(name = "date") val date: Date?
)

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
        val existingByName = getAll().map { it.name to it }
        return grades.forEach {
            val existingGrade = existingByName.firstOrNull { i -> i.first == it.name }?.second
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

