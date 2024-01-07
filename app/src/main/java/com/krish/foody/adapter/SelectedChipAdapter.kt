package com.krish.foody.adapter

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.krish.foody.R
import com.krish.foody.util.ChipClickteditemListner

class SelectedChipAdapter (
    private val context: Context,
    private val chipGroup: ChipGroup,
    private val chipItemClickListener: ChipClickteditemListner
) {
    fun addChips(chipNames: List<String>) {
        chipGroup.removeAllViews()
        for (element in chipNames) {
            val inflater = LayoutInflater.from(context)
            val chip = inflater.inflate(R.layout.item_ingredients, chipGroup, false) as Chip
            chip.text = element
            chip.isClickable = true
            chip.isCheckable = false
            chipGroup.addView(chip)
            chip.setOnClickListener {
                chipItemClickListener.chipClicked(element)
            }
        }

    }
}