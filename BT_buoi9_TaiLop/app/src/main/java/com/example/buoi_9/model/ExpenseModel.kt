package com.example.buoi_9.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ExpenseModel (
    val id: String? = null,
    val title: String? = null,
    val amount: Double? = null,
    val category: String? = null,
    val date: String? = null,
)
