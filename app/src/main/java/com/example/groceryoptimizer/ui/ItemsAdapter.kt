package com.example.groceryoptimizer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.groceryoptimizer.databinding.ItemRowBinding
import com.example.groceryoptimizer.model.Item

class ItemsAdapter(
    private val data: List<Item>,
    private val onDelete: (Long) -> Unit
) : RecyclerView.Adapter<ItemsAdapter.VH>() {

    inner class VH(val b: ItemRowBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = data[position]
        holder.b.tvName.text = item.name
        holder.b.tvMeta.text = "Qty: ${item.quantity}  •  ₹${String.format("%.2f", item.price)}" +
                (if (!item.barcode.isNullOrEmpty()) "  •  ${item.barcode}" else "")

        holder.b.btnDelete.setOnClickListener {
            onDelete(item.id)
        }
    }

    override fun getItemCount(): Int = data.size
}
