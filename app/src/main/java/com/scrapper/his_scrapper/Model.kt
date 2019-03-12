package com.scrapper.his_scrapper

import java.util.*


data class Grade(
    val name: String,
    val semester: String,
    val isPassed: Boolean,
    val creditPoints: Float?,
    val grade: Float?,
    val date: Date?
)