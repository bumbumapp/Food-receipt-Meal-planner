package com.krish.foody.ui

import androidx.recyclerview.widget.DiffUtil

class MyStringDiffCallback(private val oldList: List<String>, private val newList: List<String>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Check if items have the same content (for strings, this means they are equal)
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Contents are the same since items are strings
        return true
    }
}
