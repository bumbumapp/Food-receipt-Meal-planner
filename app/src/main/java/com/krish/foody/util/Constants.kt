package com.krish.foody.util

class Constants {
    companion object {
        val API_KEY = listOf(
            "7e121b51cf1d4ec8aa26addeb74f75c8",
            "edcf4b9bd5034ced854777aa0b9dc266",
            "b6a984492706431a927266414ab23fd9",
            "072547de4f79472b9ad1086fa53b7ee2",
            "ef68f94cbd93403f84a1c77b2fff012a",
            "6f7532590572412893b7e29555943612",
            "0ca57631982e4bd6b73ceea4e2a8e5d0",
            "0a3e2297376942239c4a800cdc1ae4c4",
            "b3eea264dfbf43e88b5caa9fed3a6f0c",
            "a02c8f1e47a149f2ac27e520a4f150a1"
        )
        var apiKeyIndex = 0
        const val BASE_URL = "https://api.spoonacular.com"
        const val BASE_IMAGE_URL = "https://spoonacular.com/cdn/ingredients_100x100/"
        const val RECIPE_RESULT_KEY = "recipesBundle"

        //API Query Keys
        const val QUERY_SEARCH = "query"
        const val QUERY_NUMBER = "number"
        const val QUERY_API_KEY = "apiKey"
        const val INGREDIENTS = "ingredients"
        const val QUERY_TYPE = "type"
        const val QUERY_DIET = "diet"
        const val QUERY_ADD_RECIPE_INFORMATION = "addRecipeInformation"
        const val QUERY_FILL_INGREDIENTS = "fillIngredients"

        // ROOM database
        const val DATABASE_NAME = "recipes_database"
        const val RECIPES_TABLE = "recipes_table"
        const val FAVORITE_RECIPES_TABLE ="favorite_recipes_table"
        const val FOOD_JOKE_TABLE = "food_joke_table"

        // Bottom Sheet and Preferences
        const val DEFAULT_RECIPES_NUMBER = "50"
        const val DEFAULT_MEAL_TYPE = "main course"
        const val DEFAULT_DIET_TYPE = "gluten free"

        const val PREFERENCES_NAME = "foody_preferences"
        const val PREFERENCES_MEAL_TYPE = "mealType"
        const val PREFERENCES_MEAL_TYPE_ID = "mealTypeId"
        const val PREFERENCES_DIET_TYPE = "dietType"
        const val PREFERENCES_DIET_TYPE_ID = "dietTypeId"
        const val PREFERENCES_BACK_ONLINE = "backOnline"


        var isEditable = false
    }
}