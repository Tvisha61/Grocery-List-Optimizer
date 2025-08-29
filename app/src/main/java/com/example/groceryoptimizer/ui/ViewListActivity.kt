package com.example.groceryoptimizer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groceryoptimizer.databinding.ActivityViewListBinding
import com.example.groceryoptimizer.db.InMemoryDb
import com.example.groceryoptimizer.model.Item

class ViewListActivity : AppCompatActivity() {
    private lateinit var b: ActivityViewListBinding
    private lateinit var adapter: ItemsAdapter
    private var items: MutableList<Item> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityViewListBinding.inflate(layoutInflater)
        setContentView(b.root)

        items = InMemoryDb.getAll().toMutableList()
        adapter = ItemsAdapter(items,
            onDelete = { id ->
                InMemoryDb.deleteById(id)
                refresh()
            }
        )

        b.rvItems.layoutManager = LinearLayoutManager(this)
        b.rvItems.adapter = adapter

        b.btnSortName.setOnClickListener {
            items.sortBy { it.name.lowercase() }
            adapter.notifyDataSetChanged()
        }

        b.btnSortPrice.setOnClickListener {
            items.sortBy { it.price }
            adapter.notifyDataSetChanged()
        }

        b.btnClearAll.setOnClickListener {
            InMemoryDb.clearAll()
            refresh()
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        items.clear()
        items.addAll(InMemoryDb.getAll())
        adapter.notifyDataSetChanged()
    }
}
