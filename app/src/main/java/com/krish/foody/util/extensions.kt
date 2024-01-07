package com.krish.foody.util

import android.content.Context
import com.krish.foody.R

fun getIngredients(context: Context):MutableList<String>{
    val itemList = mutableListOf<String>()
    val inputStream = context.resources.openRawResource(R.raw.ingredients)
    val bufferedReader = inputStream.bufferedReader()
    bufferedReader.useLines { lines ->
        lines.forEach { line ->
            itemList.add(line)
        }
    }
    inputStream.close()
    return itemList
}