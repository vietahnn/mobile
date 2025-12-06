package com.example.buoi_9.view

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.buoi_9.R
import com.example.buoi_9.databinding.ActivityMainBinding
import com.example.buoi_9.databinding.DialogAddExpenseBinding
import com.example.buoi_9.model.ExpenseModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbRef: DatabaseReference

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

        binding.btnInsert.setOnClickListener {
            callDialogExpense()
        }
        initFirebaseDatabase()

        binding.btnLoad.setOnClickListener {
            loadListExpense()
        }
    }

    private fun callDialogExpense(){
        val dialogBinding = DialogAddExpenseBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Expense")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val idItem = dbRef.push().key
                val title = dialogBinding.etTitle.text.toString()
                val amount = dialogBinding.etAmount.text.toString().toDoubleOrNull() ?: 0.0
                val category = dialogBinding.etCategory.text.toString()
                val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

                val expense = ExpenseModel(
                    id = idItem,
                    title = title,
                    amount = amount,
                    category = category,
                    date = date.toString()
                )

                //viewModel.insertExpense(expense)
                saveExpenseToFirebase(expense)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()


    }
    private fun initFirebaseDatabase() {
        dbRef = FirebaseDatabase.getInstance().getReference("Expenses")
    }
    private fun saveExpenseToFirebase(expense: ExpenseModel) {
        dbRef.child(expense.id?: "").setValue(expense)
            .addOnSuccessListener {
                Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadListExpense(){
        val expenses = mutableListOf<ExpenseModel>() //nâng cấp dùng cho CardView với RecyclerView
        val listExpense = mutableListOf<String>()
        val lvExpenses = binding.lvExpenses

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    expenses.clear()//nâng cấp dùng cho CardView với RecyclerView
                    listExpense.clear()
                    var dem = 0
                    for (expenseSnapshot in dataSnapshot.children) {
                        dem++
                        expenseSnapshot.getValue(ExpenseModel::class.java)?.let {expense ->
                            expenses.add(expense) //nâng cấp dùng cho CardView với RecyclerView
                            listExpense.add("${expense.category}: ${expense.title} - ${expense.amount} on ${expense.date}")
                        }

                        //expenses.add(expense!!) //nâng cấp dùng cho CardView với RecyclerView
                        //listExpense.add("${expense!!.category}: ${expense.title} - ${expense.amount}")
                    }
                    //val adapter = ExpenseAdapter(this, expenses) //nâng cấp dùng cho CardView với RecyclerView
                    val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, listExpense)
                    lvExpenses.adapter = adapter

                    Toast.makeText(this@MainActivity, "Loaded $dem expenses", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }
}