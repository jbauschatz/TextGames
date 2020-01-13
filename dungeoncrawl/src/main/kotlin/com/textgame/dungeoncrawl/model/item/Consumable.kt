package com.textgame.dungeoncrawl.model.item

import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.verb.Verb

class Consumable(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns = Pronouns.THIRD_PERSON_SINGULAR_NEUTER,
        val verb: Verb,
        val healing: Int
): Item(id, name, pronouns)