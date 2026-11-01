package com.krish.foody.util

/**
 * Spoonacular returns recipe photos as e.g. ".../716429-312x231.jpg", which looks blurry on modern
 * screens. The same image exists at 636x393, the largest size the API serves.
 */
object RecipeImage {
    private val SIZE = Regex("-\\d+x\\d+\\.(jpe?g|png)$", RegexOption.IGNORE_CASE)

    fun hiRes(url: String?): String? =
        url?.takeIf { it.contains("spoonacular.com") }?.replace(SIZE) { "-636x393.${it.groupValues[1]}" } ?: url
}
