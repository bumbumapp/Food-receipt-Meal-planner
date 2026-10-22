package com.krish.foody.di

import com.krish.foody.models.FoodRecipe
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit

/** Regression: recipes list was always "Recipes not found" because the Moshi converter rejected the Kotlin models. */
class RecipeConverterTest {

    @Test
    fun recipeResponseIsParsedByTheProvidedConverter() {
        val factory = NetworkModule.provideConverterFactory()
        val retrofit = Retrofit.Builder().baseUrl("https://example.com/").addConverterFactory(factory).build()
        val converter = factory.responseBodyConverter(FoodRecipe::class.java, emptyArray(), retrofit)!!
        val json = """{"results":[{"id":716429,"title":"Pasta","image":"x.jpg","readyInMinutes":45,"vegan":false}]}"""
        val recipe = converter.convert(ResponseBody.create(MediaType.parse("application/json"), json)) as FoodRecipe
        assertEquals(1, recipe.results.size)
        assertEquals(716429, recipe.results[0].id)
        assertEquals("Pasta", recipe.results[0].title)
    }
}
