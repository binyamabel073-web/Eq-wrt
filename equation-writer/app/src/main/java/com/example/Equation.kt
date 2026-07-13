package com.example

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_equations")
data class Equation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val unicodeContent: String,
    val latexContent: String,
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis(),
    val bgThemeName: String = "Elegant Dark",
    val isSerifFont: Boolean = true,
    val fontSizeScale: Float = 1.0f
)
