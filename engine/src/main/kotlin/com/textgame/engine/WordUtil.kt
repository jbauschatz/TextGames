package com.textgame.engine

class WordUtil {

    companion object {
        fun startsWithVowel(word: String) =
                word.isNotEmpty() && "AEIOUaeiou".contains(word[0])
    }
}