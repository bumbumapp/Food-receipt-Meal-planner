package com.krish.foody.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.krish.foody.R
import com.krish.foody.util.ChipItemClickListener

class ChipAdapter(
    private val context: Context,
    private val chipGroup: ChipGroup,
    private val chipItemClickListener: ChipItemClickListener) {

    private var chips:MutableList<String>  = mutableListOf()
    fun addChips(
        chipNames: List<String>,
        maxItemsToShow: Int,
        listOfSelectedIngredinets: MutableList<String>
    ) {
        chips = chipNames.toMutableList()
        chipGroup.removeAllViews()
        for (i in 0 until minOf(maxItemsToShow, chipNames.size)) {
            val inflater = LayoutInflater.from(context)
            val chip = inflater.inflate(R.layout.item_ingredients, chipGroup, false) as Chip
            chip.text = chipNames[i]
            chip.isClickable = true
            chip.isCheckable = true
            chipGroup.addView(chip)
            if (listOfSelectedIngredinets.contains(chip.text.toString().lowercase())){
                 chip.isChecked = true
            }
            chip.setOnClickListener {
                chipItemClickListener.clickItemForSelect(chip.text.toString())
            }

        }


        if (574 > maxItemsToShow) {
            val inflater = LayoutInflater.from(context)
            val moreChip = inflater.inflate(R.layout.item_ingredients, chipGroup, false) as Chip
            moreChip.text = "${574 - maxItemsToShow}+ more"
            moreChip.isClickable = true
            moreChip.isCheckable = false
            chipGroup.addView(moreChip)
            moreChip.setOnClickListener {
              chipGroup.removeView(it)
              chipItemClickListener.itemClicked(maxItemsToShow)
            }
        }
    }

}
