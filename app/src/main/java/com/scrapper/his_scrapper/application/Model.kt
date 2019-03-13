package com.scrapper.his_scrapper.application

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "grades")
data class Grade(
    @PrimaryKey(autoGenerate = true) var uid: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "semester") val semester: String,
    @ColumnInfo(name = "is_passed") val passed: Boolean,
    @ColumnInfo(name = "credits") val credits: Float? = null,
    @ColumnInfo(name = "grade") val grade: Float? = null,
    @ColumnInfo(name = "date") val date: Date? = null
)

enum class Reason {
    NONE,
    CREDENTIALS,
    PAGE
}

data class HisServiceResult(
    val grades: List<Grade> = listOf(),
    val success: Boolean = false,
    val reason: Reason = Reason.NONE
)