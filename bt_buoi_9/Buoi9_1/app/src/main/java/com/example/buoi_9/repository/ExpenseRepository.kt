package com.example.buoi_9.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.buoi_9.model.ExpenseModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExpenseRepository {
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Expenses")

    private val _expenses = MutableLiveData<List<ExpenseModel>>()
    val expenses: LiveData<List<ExpenseModel>> = _expenses

    private val _operationStatus = MutableLiveData<OperationStatus>()
    val operationStatus: LiveData<OperationStatus> = _operationStatus

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val expenseList = mutableListOf<ExpenseModel>()
                if (dataSnapshot.exists()) {
                    for (expenseSnapshot in dataSnapshot.children) {
                        expenseSnapshot.getValue(ExpenseModel::class.java)?.let { expense ->
                            expenseList.add(expense)
                        }
                    }
                }
                _expenses.value = expenseList
            }

            override fun onCancelled(error: DatabaseError) {
                _operationStatus.value = OperationStatus.Error(error.message)
            }
        })
    }

    fun addExpense(expense: ExpenseModel) {
        dbRef.child(expense.id ?: "").setValue(expense)
            .addOnSuccessListener {
                _operationStatus.value = OperationStatus.Success("Expense added successfully")
            }
            .addOnFailureListener { e ->
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to add expense")
            }
    }

    fun updateExpense(expense: ExpenseModel) {
        dbRef.child(expense.id ?: "").setValue(expense)
            .addOnSuccessListener {
                _operationStatus.value = OperationStatus.Success("Expense updated successfully")
            }
            .addOnFailureListener { e ->
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to update expense")
            }
    }

    fun deleteExpense(expenseId: String) {
        dbRef.child(expenseId).removeValue()
            .addOnSuccessListener {
                _operationStatus.value = OperationStatus.Success("Expense deleted successfully")
            }
            .addOnFailureListener { e ->
                _operationStatus.value = OperationStatus.Error(e.message ?: "Failed to delete expense")
            }
    }

    sealed class OperationStatus {
        data class Success(val message: String) : OperationStatus()
        data class Error(val message: String) : OperationStatus()
    }
}

