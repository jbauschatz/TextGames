package com.textgame.dungeoncrawl

/**
 * Selects between equally likely alternatives.
 *
 * Only the chosen alternative is executed
 */
fun <T> pick(vararg options: () -> T): T =
        options[((Math.random() * options.size).toInt())]()

/**
 * Selects between equally likely alternatives.
 */
fun <T> pick(vararg options: T): T =
        options[((Math.random() * options.size).toInt())]
/**
 * Selects between equally likely alternatives.
 */
fun <T> pick(options: List<T>): T =
        options[((Math.random() * options.size).toInt())]

/**
 * Executes the given function only if a random number is less than [percent]
 */
fun ifPercent(percent: Int, function: () -> Unit) {
    if (isPercent(percent))
        function()
}

/**
 * Returns whether a random number is less than [percent]
 */
fun isPercent(percent: Int) = Math.random() * 100 < percent