package com.textgame.dungeoncrawl.model.item

import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.verb.Verb

class Weapon(
        id: Int,
        name: NounPhrase,
        val attackVerbs: List<Verb>,
        pronouns: Pronouns = Pronouns.THIRD_PERSON_SINGULAR_NEUTER
): Item(id, name, pronouns)