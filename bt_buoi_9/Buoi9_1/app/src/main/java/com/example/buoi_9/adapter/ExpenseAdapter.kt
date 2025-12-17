package com.example.buoi_9.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.buoi_9.R
import com.example.buoi_9.model.ExpenseModel
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.Locale

class ExpenseAdapter(
    private var expenses: List<ExpenseModel>,
    private val onItemClick: (ExpenseModel) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCategoryIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)

        fun bind(expense: ExpenseModel) {
            tvTitle.text = expense.title
            tvCategory.text = expense.category
            tvDate.text = expense.date

            // Format amount with currency
            val locale = Locale.Builder().setLanguage("vi").setRegion("VN").build()
            val formattedAmount = NumberFormat.getCurrencyInstance(locale)
                .format(expense.amount)
            tvAmount.text = formattedAmount

            // Load image from URL using Picasso
            if (!expense.imageUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(expense.imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivCategoryIcon)
            } else {
                // Set default icon based on category
                ivCategoryIcon.setImageResource(getDefaultCategoryIcon(expense.category))
            }

            itemView.setOnClickListener {
                onItemClick(expense)
            }
        }

        private fun getDefaultCategoryIcon(category: String?): Int {
            return when (category?.lowercase()) {
                "food", "ăn uống" -> android.R.drawable.ic_menu_myplaces
                "transport", "di chuyển" -> android.R.drawable.ic_menu_directions
                "shopping", "mua sắm" -> android.R.drawable.ic_menu_add
                "entertainment", "giải trí" -> android.R.drawable.ic_menu_slideshow
                "health", "sức khỏe" -> android.R.drawable.ic_menu_agenda
                "education", "giáo dục" -> android.R.drawable.ic_menu_info_details
                else -> android.R.drawable.ic_menu_gallery
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount(): Int = expenses.size

    fun updateExpenses(newExpenses: List<ExpenseModel>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}

