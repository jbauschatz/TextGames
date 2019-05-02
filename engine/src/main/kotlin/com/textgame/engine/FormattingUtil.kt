package com.textgame.engine

class FormattingUtil {

    companion object {

        fun formatList(items: List<String>, conjunction: String = "and"): String {
            if (items.size == 1)
                return items[0]

            if (items.size == 2)
                return "${items[0]} $conjunction ${items[1]}"

            var string = "";
            for (i in 0 until items.size-1)
                string += "${items[i]}, "
            return string + conjunction + " " + items[items.size-1]
        }
    }
}