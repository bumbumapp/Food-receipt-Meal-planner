package com.krish.foody.ui.fragments.recipes

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.krish.foody.adapter.BtnClick
import com.krish.foody.adapter.RBtnClick
import com.krish.foody.databinding.AdapterForingredientsrecipesBinding
import com.krish.foody.models.FoodRecipe
import com.krish.foody.models.FoodRecipes
import com.krish.foody.models.Results
import com.krish.foody.ui.fragments.SearchIngreientFragmentDirections
import com.krish.foody.util.RecipesDiffUtil
import kotlin.math.roundToInt

class IngredientsAdapter(
   private val btnClick: RBtnClick,
) : RecyclerView.Adapter<IngredientsAdapter.RecipeViewHolder>() {

    private var recipes = emptyList<com.krish.foody.models.Result>()

    class RecipeViewHolder(private val binding: AdapterForingredientsrecipesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(result: com.krish.foody.models.Result) {
            binding.recipeTime.text = result.readyInMinutes.toString()
            binding.recipeLikes.text = result.aggregateLikes.toString()
            binding.recipesTitle.text = result.title.toString()
            binding.image.load(result.image)
        }

        companion object {
            fun from(parent: ViewGroup): RecipeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AdapterForingredientsrecipesBinding.inflate(layoutInflater, parent, false)
                return RecipeViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {
        return RecipeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)
        holder.itemView.setOnClickListener {
            btnClick.getRecipeOnClick(currentRecipe)
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setData(newData: List<com.krish.foody.models.Result>) {
        val recipesDiffUtil = RecipesDiffUtil(recipes, newData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

}