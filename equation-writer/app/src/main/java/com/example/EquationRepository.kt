package com.example

import kotlinx.coroutines.flow.Flow

class EquationRepository(private val equationDao: EquationDao) {
    val allEquations: Flow<List<Equation>> = equationDao.getAllEquations()

    suspend fun insert(equation: Equation) {
        equationDao.insertEquation(equation)
    }

    suspend fun delete(equation: Equation) {
        equationDao.deleteEquation(equation)
    }

    suspend fun deleteById(id: Int) {
        equationDao.deleteEquationById(id)
    }
}
