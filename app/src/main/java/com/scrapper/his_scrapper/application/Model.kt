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
    @ColumnInfo(name = "credits") val credits: Float?,
    @ColumnInfo(name = "grade") val grade: Float?,
    @ColumnInfo(name = "date") val date: Date?
)