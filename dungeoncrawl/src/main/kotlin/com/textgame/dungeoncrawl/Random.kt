package com.textgame.dungeoncrawl

fun <T> pick(vararg options: () -> T): T =
        options[((Math.random() * options.size).toInt())]()

fun <T> pick(vararg options: T): T =
        options[((Math.random() * options.size).toInt())]

fun ifPercent(percent: Int, function: () -> Unit) {
    if (Math.random() * 100 < percent)
        function()
}