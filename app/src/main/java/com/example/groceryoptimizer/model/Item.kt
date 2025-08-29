package com.example.groceryoptimizer.model

data class Item(
    val id: Long,            // ← add this
    val name: String,
    val quantity: Int,
    val price: Double,
    val barcode: String? = null   // ← add this
)
