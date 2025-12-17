package com.example.buoi_9.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buoi_9.R
import com.example.buoi_9.adapter.ExpenseAdapter
import com.example.buoi_9.databinding.ActivityMainBinding
import com.example.buoi_9.databinding.DialogAddExpenseBinding
import com.example.buoi_9.model.ExpenseModel
import com.example.buoi_9.repository.ExpenseRepository
import com.example.buoi_9.viewmodel.ExpenseViewModel
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewModel()
        setupRecyclerView()
        observeViewModel()

        binding.btnInsert.setOnClickListener {
            showAddExpenseDialog()
        }

        binding.btnLoad.setOnClickListener {
            Toast.makeText(this, "Expenses loaded automatically", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(emptyList()) { expense ->
            showExpenseDetails(expense)
        }
        binding.rvExpenses.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = expenseAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.expenses.observe(this) { expenses ->
            expenseAdapter.updateExpenses(expenses)
            Toast.makeText(this, "Loaded ${expenses.size} expenses", Toast.LENGTH_SHORT).show()
        }

        viewModel.operationStatus.observe(this) { status ->
            when (status) {
                is ExpenseRepository.OperationStatus.Success -> {
                    Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                }
                is ExpenseRepository.OperationStatus.Error -> {
                    Toast.makeText(this, status.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showAddExpenseDialog() {
        val dialogBinding = DialogAddExpenseBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Expense")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val idItem = FirebaseDatabase.getInstance().getReference("Expenses").push().key
                val title = dialogBinding.etTitle.text.toString()
                val amount = dialogBinding.etAmount.text.toString().toDoubleOrNull() ?: 0.0
                val category = dialogBinding.etCategory.text.toString()
                val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

                // Get image URL from input or auto-generate based on category
                val inputImageUrl = dialogBinding.etImageUrl.text.toString().trim()
                val imageUrl = if (inputImageUrl.isNotEmpty()) {
                    inputImageUrl
                } else {
                    getCategoryImageUrl(category)
                }

                val expense = ExpenseModel(
                    id = idItem,
                    title = title,
                    amount = amount,
                    category = category,
                    date = date,
                    imageUrl = imageUrl
                )

                viewModel.addExpense(expense)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun showExpenseDetails(expense: ExpenseModel) {
        AlertDialog.Builder(this)
            .setTitle(expense.title)
            .setMessage(
                "Category: ${expense.category}\n" +
                "Amount: ${expense.amount}\n" +
                "Date: ${expense.date}"
            )
            .setPositiveButton("OK", null)
            .setNegativeButton("Delete") { _, _ ->
                expense.id?.let { viewModel.deleteExpense(it) }
            }
            .show()
    }

    private fun getCategoryImageUrl(category: String): String {
        // Map categories to image URLs
        return when (category.lowercase()) {
            "food", "ăn uống" -> "https://cdn-icons-png.flaticon.com/512/3703/3703377.png"
            "transport", "di chuyển" -> "https://cdn-icons-png.flaticon.com/512/3448/3448339.png"
            "shopping", "mua sắm" -> "https://cdn-icons-png.flaticon.com/512/2331/2331970.png"
            "entertainment", "giải trí" -> "https://cdn-icons-png.flaticon.com/512/3774/3774278.png"
            "health", "sức khỏe" -> "https://cdn-icons-png.flaticon.com/512/2966/2966327.png"
            "education", "giáo dục" -> "https://cdn-icons-png.flaticon.com/512/3976/3976625.png"
            else -> "https://cdn-icons-png.flaticon.com/512/1198/1198334.png"
        }
    }
}