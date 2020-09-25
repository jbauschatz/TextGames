package com.textgame.dungeoncrawl.model.item

import com.textgame.dungeoncrawl.english.AttackSentenceSupplier
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.Pronouns
import com.textgame.engine.model.verb.Verb

class Weapon(
        id: Int,
        name: NounPhrase,
        val attackSentenceSuppliers: List<AttackSentenceSupplier>,
        val attackMissSentenceSuppliers: List<AttackSentenceSupplier>,
        pronouns: Pronouns = Pronouns.THIRD_PERSON_SINGULAR_NEUTER
): Item(id, name, pronouns)