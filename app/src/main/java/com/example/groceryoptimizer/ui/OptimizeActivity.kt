package com.example.groceryoptimizer.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.groceryoptimizer.databinding.ActivityOptimizeBinding
import com.example.groceryoptimizer.db.InMemoryDb
import com.example.groceryoptimizer.model.Item

class OptimizeActivity : AppCompatActivity() {

    private lateinit var b: ActivityOptimizeBinding
    private val budget = 500.0  // Example budget, can be changed
    private lateinit var items: List<Item>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityOptimizeBinding.inflate(layoutInflater)
        setContentView(b.root)

        // ✅ Initialize DB if not done yet
        InMemoryDb.init(this)

        // ✅ Fetch items from DB
        items = InMemoryDb.getAll()

        compute()
    }

    private fun compute() {
        if (items.isEmpty()) {
            b.tvSummary.text = "No items found in database."
            return
        }

        val totalCost = items.sumOf { it.price * it.quantity }
        val avgPrice = items.map { it.price }.average()

        val summary = buildString {
            appendLine("Items: ${items.size}")
            appendLine("Average Unit Price: ₹${String.format("%.2f", avgPrice)}")
            appendLine("Total Cost (Σ price × qty): ₹${String.format("%.2f", totalCost)}")
            if (totalCost > budget) {
                appendLine("⚠️ Budget Exceeded! Limit ₹${String.format("%.2f", budget)}")
            }
        }

        b.tvSummary.text = summary

        val expensive = items.filter { it.price > avgPrice }
        b.containerExpensive.removeAllViews()

        if (expensive.isEmpty()) {
            addText("No expensive items (all at or below average).")
        } else {
            addHeader("Expensive Items (> average)")
            expensive.forEach {
                addText("- ${it.name} = ₹${String.format("%.2f", it.price)} (qty ${it.quantity})")
            }
        }
    }

    private fun addHeader(text: String) {
        val tv = TextView(this)
        tv.text = text
        tv.textSize = 18f
        tv.setPadding(8, 16, 8, 8)
        b.containerExpensive.addView(tv)
    }

    private fun addText(text: String) {
        val tv = TextView(this)
        tv.text = text
        tv.textSize = 16f
        tv.setPadding(16, 8, 8, 8)
        b.containerExpensive.addView(tv)
    }
}
