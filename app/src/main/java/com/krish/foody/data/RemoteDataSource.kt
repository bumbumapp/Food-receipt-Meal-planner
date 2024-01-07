package com.krish.foody.data

import com.krish.foody.data.database.entities.FoodJokeEntity
import com.krish.foody.data.network.FoodRecipeApi
import com.krish.foody.models.*
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipeApi: FoodRecipeApi
) {

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return foodRecipeApi.getRecipes(queries)
    }

    suspend fun searchRecipes(searchQuery : Map<String , String>) : Response<FoodRecipe>{
        return foodRecipeApi.searchRecipes(searchQuery)
    }

    suspend fun getFoodJoke(apiKey : String) : Response<FoodJoke>{
        return foodRecipeApi.getFoodJoke(apiKey)
    }

}