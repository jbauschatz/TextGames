package com.textgame.engine

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class FormattingUtilTest {

    @Test
    fun formatList_singleItem() {
        assertThat(
                FormattingUtil.formatList(listOf("One"), "and"),
                equalTo("One")
        )
    }

    @Test
    fun formatList_twoItems() {
        assertThat(
                FormattingUtil.formatList(listOf("One", "Two"), "and"),
                equalTo("One and Two")
        )
    }

    @Test
    fun formatList_threeItems() {
        assertThat(
                FormattingUtil.formatList(listOf("One", "Two", "Three"), "and"),
                equalTo("One, Two, and Three")
        )
    }
}