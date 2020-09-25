package com.textgame.dungeoncrawl.english

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.ProperNoun
import com.textgame.engine.model.verb.Verb


/**
 * Entity to be used when narrating "there is" sentences.
 */
val THERE = NamedEntity(NamedEntity.nextId(), ProperNoun("there"), null)

val IS = Verb("is", "are")
val SEE = Verb("sees", "see")
val GO = Verb("goes", "go")
val EXIT = Verb("exits", "exit")
val ENTER = Verb("enters", "enter")
val TAKE = Verb("takes", "take")

/**
 * A door "leads" in a direction
 */
val LEADS = Verb("leads", "lead")

val DRAW = Verb("draws", "draw")
val SHEATHE = Verb("sheathes", "sheathe")

val ATTACK = Verb("attacks", "attack")
val SLASH = Verb("slashes", "slash")
val BLUDGEON = Verb("bludgeons", "bludgeon")
val HEW = Verb("hews", "hew")
val STAB = Verb("stabs", "stab")
val KILL = Verb("kills", "kill")
val MISS = Verb("misses", "miss")

val WIELD = Verb("wields", "wield")
