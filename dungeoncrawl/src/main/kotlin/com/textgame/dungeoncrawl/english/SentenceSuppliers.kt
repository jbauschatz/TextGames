package com.textgame.dungeoncrawl.english

import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.dungeoncrawl.view.WeaponView
import com.textgame.engine.model.predicate.VerbPredicate
import com.textgame.engine.model.preposition.PrepositionalPhrase
import com.textgame.engine.model.sentence.SimpleSentence
import com.textgame.engine.model.verb.Verb

typealias AttackSentenceSupplier = (CreatureView, CreatureView, WeaponView) -> SimpleSentence

fun buildVerbWithWeaponSupplier(verb: Verb): AttackSentenceSupplier {
    return { attacker, defender, weapon ->
        SimpleSentence(attacker, VerbPredicate(verb, defender, PrepositionalPhrase("with", weapon)))
    }
}

fun buildVerbWeaponAtSupplier(verb: Verb): AttackSentenceSupplier {
    return { attacker, defender, weapon ->
        SimpleSentence(attacker, VerbPredicate(verb, weapon, PrepositionalPhrase("at", defender)))
    }
}

fun buildWeaponVerbSupplier(verb: Verb): AttackSentenceSupplier {
    return { _, defender, weapon ->
        SimpleSentence(weapon, VerbPredicate(verb, defender))
    }
}

val ATTACK_WITH_WEAPON = buildVerbWithWeaponSupplier(ATTACK)
val SLASH_WITH_WEAPON = buildVerbWithWeaponSupplier(SLASH)
val STRIKE_WITH_WEAPON = buildVerbWithWeaponSupplier(STRIKE)
val CUT_WITH_WEAPON = buildVerbWithWeaponSupplier(CUT)
val BLUDGEON_WITH_WEAPON = buildVerbWithWeaponSupplier(BLUDGEON)
val STAB_WITH_WEAPON = buildVerbWithWeaponSupplier(STAB)
val HEW_WITH_WEAPON = buildVerbWithWeaponSupplier(HEW)
val HACK_WITH_WEAPON = buildVerbWithWeaponSupplier(HACK)
val SMASH_WITH_WEAPON = buildVerbWithWeaponSupplier(SMASH)
val SHOOT_WITH_WEAPON = buildVerbWithWeaponSupplier(SHOOT)

val WEAPON_CRASH_INTO = buildWeaponVerbSupplier(CRASH_INTO)
val WEAPON_STRIKE = buildWeaponVerbSupplier(STRIKE)
val WEAPON_CUT = buildWeaponVerbSupplier(CUT)

val THRUST_WEAPON_AT = buildVerbWeaponAtSupplier(THRUST)
val SWING_WEAPON_AT = buildVerbWeaponAtSupplier(SWING)
val FIRE_WEAPON_AT = buildVerbWeaponAtSupplier(FIRE)