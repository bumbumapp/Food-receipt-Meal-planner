package com.krish.foody.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.krish.foody.R
import com.krish.foody.ui.MyStringDiffCallback
import com.krish.foody.ui.fragments.SearchAdapterItemClickedListener

class ItemAdapter(var searchAdapterItemClickedListener: SearchAdapterItemClickedListener) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    private  var items: List<String> = emptyList()

    fun updateData(newItems: List<String>) {
        val diffResult = DiffUtil.calculateDiff(MyStringDiffCallback(items, newItems))
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_search_suggestionb, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item
        holder.itemView.setOnClickListener{
            searchAdapterItemClickedListener.addItem(item)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }
}
