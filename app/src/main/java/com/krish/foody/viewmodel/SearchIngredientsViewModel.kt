package com.krish.foody.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.krish.foody.data.Repository
import com.krish.foody.models.FoodRecipe
import com.krish.foody.models.FoodRecipes
import com.krish.foody.models.Ingredients
import com.krish.foody.models.ResponseItem
import com.krish.foody.util.Constants
import com.krish.foody.util.Constants.Companion.apiKeyIndex
import com.krish.foody.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchIngredientsViewModel@Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _selectedIngredinets = MutableLiveData<MutableList<String>>(mutableListOf())
    var selectedIngredinets = _selectedIngredinets
    val listOfSelectedIngredinets = mutableListOf<String>()
    var _recipeResponse: Channel<NetworkResult<FoodRecipe>> = Channel()
    var recipeResponse = _recipeResponse.receiveAsFlow()

    fun setSelectedIngredient(selectedIngredient:String,isEditText:Boolean){
        if (!listOfSelectedIngredinets.contains(selectedIngredient)){
             listOfSelectedIngredinets.add(selectedIngredient)
            _selectedIngredinets.postValue(listOfSelectedIngredinets)
            return
        }
        if (listOfSelectedIngredinets.contains(selectedIngredient) && !isEditText){
            listOfSelectedIngredinets.remove(selectedIngredient)
            _selectedIngredinets.postValue(listOfSelectedIngredinets)
        }

    }
    fun searchRecipes() {
        var searchQuery = ""
        viewModelScope.launch {
            listOfSelectedIngredinets.forEach {
                searchQuery+="$it,"
            }
            getIngredientsSafeCall(applySearchQuery(searchQuery))
        }
    }
    private fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[Constants.QUERY_SEARCH] = searchQuery
        queries[Constants.QUERY_NUMBER] = Constants.DEFAULT_RECIPES_NUMBER
        queries[Constants.QUERY_API_KEY] = Constants.API_KEY[apiKeyIndex]
        queries[Constants.QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[Constants.QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

    private suspend fun getIngredientsSafeCall(queries: Map<String, String>) {
        _recipeResponse.trySend(NetworkResult.Loading())
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(queries)
                _recipeResponse.trySend(handleFoodRecipeResponse(response))
            } catch (e: Exception) {
                _recipeResponse.trySend(NetworkResult.Error(e.message.toString()))
            }
        } else {
            _recipeResponse.trySend(NetworkResult.Error("No Internet Connection"))
        }
    }

    private fun handleFoodRecipeResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                if(apiKeyIndex == 9){
                    apiKeyIndex = 0
                }else apiKeyIndex += 1
                searchRecipes()
                return NetworkResult.Error("Loading...")
            }
            response.body()?.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager


        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }


}