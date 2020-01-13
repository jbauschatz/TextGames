package com.textgame.dungeoncrawl.model.item

import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns

class Weapon(
        id: Int,
        name: NounPhrase,
        pronouns: Pronouns = Pronouns.THIRD_PERSON_SINGULAR_NEUTER
): Item(id, name, pronouns)