package com.textgame.dungeoncrawl.english

import com.textgame.dungeoncrawl.view.CreatureView
import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.ProperNoun
import com.textgame.engine.model.sentence.SimpleSentence
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
val STRIKE = Verb("strikes", "strike")
val CUT = Verb("cuts", "cut")
val BLUDGEON = Verb("bludgeons", "bludgeon")
val STAB = Verb("stabs", "stab")
val HEW = Verb("hews", "hew")
val HACK = Verb("hacks", "hack")
val SHOOT = Verb("shoots", "shoot")
val SMASH = Verb("smashes", "smash")
val CRASH_INTO = Verb("crashes into", "crash into")

val SWING = Verb("swings", "swing")
val THRUST = Verb("thrusts", "trust")
val FIRE = Verb("fires", "fire")

val KILL = Verb("kills", "kill")
val MISS = Verb("misses", "miss")
val EVADE = Verb("evades", "evade")

val WIELD = Verb("wields", "wield")
