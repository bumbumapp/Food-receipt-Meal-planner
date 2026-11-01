package com.krish.foody.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RecipeImageTest {
    @Test
    fun upgradesSpoonacularSize() {
        assertEquals("https://img.spoonacular.com/recipes/716429-636x393.jpg",
            RecipeImage.hiRes("https://img.spoonacular.com/recipes/716429-312x231.jpg"))
        assertEquals("https://spoonacular.com/recipeImages/1-636x393.png",
            RecipeImage.hiRes("https://spoonacular.com/recipeImages/1-556x370.png"))
    }

    @Test
    fun leavesOtherUrlsAlone() {
        assertEquals("https://example.com/a-312x231.jpg", RecipeImage.hiRes("https://example.com/a-312x231.jpg"))
        assertNull(RecipeImage.hiRes(null))
    }
}
