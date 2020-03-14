package com.textgame.engine.model.nounphrase

import com.textgame.engine.test.TestNamedEntity
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class PartialNameMatcherTest {

    companion object {

        @JvmStatic
        fun matches_arguments(): Stream<Arguments> = Stream.of(
                Arguments.of("Jack", ProperNoun("Jack"), true),
                Arguments.of("jack", ProperNoun("Jack"), true),
                Arguments.of("Jill", ProperNoun("Jack"), false),

                Arguments.of("key", Noun("key"), true),
                Arguments.of("KEY", Noun("key"), true),
                Arguments.of("spoon", Noun("key"), false),

                Arguments.of("gold coin", Adjective("gold", Noun("coin")), true),
                Arguments.of("Gold Coin", Adjective("gold", Noun("coin")), true),
                Arguments.of("coin", Adjective("gold", Noun("coin")), true),
                Arguments.of("Coin", Adjective("gold", Noun("coin")), true),
                Arguments.of("spoon", Adjective("gold", Noun("coin")), false)
        )
    }

    @ParameterizedTest(name = "{index} name = {0}, nounPhrase = {1}, expect match = {2}")
    @MethodSource("matches_arguments")
    fun matches_parameterized(name: String, nounPhrase: NounPhrase, expectMatch: Boolean) {
        val matchString = if (expectMatch) "match" else "not match"
        MatcherAssert.assertThat(
                "Name \"$name\" should $matchString nounPhrase: $nounPhrase",
                PartialNameMatcher.matches(name, nounPhrase),
                equalTo(expectMatch)
        )
    }

//    @Test
//    fun findByName_oneExactMatch() {
//        // GIVEN an List containing multiple NamedEntities
//        val item1 = TestNamedEntity(1, Noun("item1"))
//        val items = listOf(
//                item1,
//                TestNamedEntity(1, Noun("item2"))
//        )
//
//        // WHEN searching by the exact name of an item
//        val matches = PartialNameMatcher.findByName("item1", items)
//
//        // EXPECT the item to be found
//        MatcherAssert.assertThat(matches, contains(item1))
//    }
//
//    @Test
//    fun findByName_oneMatchIgnoreCase() {
//        // GIVEN an List containing multiple NamedEntities
//        val item1 = TestNamedEntity(1, Noun("item1"))
//        val items = listOf(
//                item1,
//                TestNamedEntity(2, Noun("item2"))
//        )
//
//        // WHEN searching by the name of an item, differently capitalized
//        val matches = PartialNameMatcher.findByName("ITEM1", items)
//
//        // EXPECT the item to be found
//        MatcherAssert.assertThat(matches, contains(item1))
//    }
//
//    @Test
//    fun findByName_multipleExactMatches() {
//        // GIVEN an List containing multiple NamedEntities
//        val item1 = TestNamedEntity(1, Noun("fork"))
//        val item2 = TestNamedEntity(2, Noun("fork"))
//        val items = listOf(
//                item1,
//                item2,
//                TestNamedEntity(3, Noun("spoon"))
//        )
//
//        // WHEN searching by the name of multiple items
//        val matches = PartialNameMatcher.findByName("fork", items)
//
//        // EXPECT the items to be found
//        MatcherAssert.assertThat(matches, contains(item1, item2))
//    }
//
//    @Test
//    fun findByName_multiplePartialMatches() {
//        // GIVEN an List containing multiple NamedEntities
//        val item1 = TestNamedEntity(1, Adjective("silver", Noun("fork")))
//        val item2 = TestNamedEntity(2, Adjective("plastic", Noun("fork")))
//        val items = listOf(
//                item1,
//                item2,
//                TestNamedEntity(3, Noun("spoon"))
//        )
//
//        // WHEN searching by the partial name of multiple items
//        val matches = PartialNameMatcher.findByName("fork", items)
//
//        // EXPECT the items to be found
//        MatcherAssert.assertThat(matches, contains(item1, item2))
//    }
//
//    @Test
//    fun findByName_noMatches() {
//        // GIVEN an List containing multiple NamedEntities
//        val items = listOf(
//                TestNamedEntity(1, Noun("fork")),
//                TestNamedEntity(2, Noun("spoon"))
//        )
//
//        // WHEN searching by a name that does not match
//        val matches = PartialNameMatcher.findByName("knife", items)
//
//        // EXPECT no items to be found
//        MatcherAssert.assertThat(matches, empty())
//    }

}