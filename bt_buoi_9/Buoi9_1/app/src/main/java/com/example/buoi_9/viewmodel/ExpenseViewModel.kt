package com.example.buoi_9.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.buoi_9.model.ExpenseModel
import com.example.buoi_9.repository.ExpenseRepository

class ExpenseViewModel : ViewModel() {
    private val repository = ExpenseRepository()

    val expenses: LiveData<List<ExpenseModel>> = repository.expenses
    val operationStatus: LiveData<ExpenseRepository.OperationStatus> = repository.operationStatus

    fun addExpense(expense: ExpenseModel) {
        repository.addExpense(expense)
    }

    fun updateExpense(expense: ExpenseModel) {
        repository.updateExpense(expense)
    }

    fun deleteExpense(expenseId: String) {
        repository.deleteExpense(expenseId)
    }
}

