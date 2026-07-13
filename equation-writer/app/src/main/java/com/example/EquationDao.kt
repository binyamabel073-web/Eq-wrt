package com.example

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EquationDao {
    @Query("SELECT * FROM saved_equations ORDER BY timestamp DESC")
    fun getAllEquations(): Flow<List<Equation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquation(equation: Equation)

    @Delete
    suspend fun deleteEquation(equation: Equation)

    @Query("DELETE FROM saved_equations WHERE id = :id")
    suspend fun deleteEquationById(id: Int)
}
