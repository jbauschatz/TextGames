package com.textgame.engine.narrator

import com.textgame.engine.model.NamedEntity
import com.textgame.engine.model.nounphrase.NounPhrase
import com.textgame.engine.model.nounphrase.NounPhraseFormatter
import com.textgame.engine.model.sentence.SimpleSentence
import java.lang.StringBuilder

class Narrator(
        private val narrativeContext: NarrativeContext
) {

    fun writeSentence(sentence: SimpleSentence): String {
        val subjectName = referToByName(sentence.subject)

        // Start with basic Subject/Verb
        val builder = StringBuilder()
                .append(NounPhraseFormatter.format(subjectName, true))
                .append(" ")
                .append(sentence.verb)

        // Include the Direct Object (if present)
        sentence.directObject?.let {
            // If the Subject and Object are the same, use the reflexive pronoun
            val formattedObjectName =
                    if (sentence.directObject === sentence.subject)
                        sentence.directObject.getPronouns().reflexive
                    else
                        NounPhraseFormatter.format(referToByName(sentence.directObject), false)

            builder.append(" ")
                    .append(formattedObjectName)
        }

        // Apply punctuation at the end of the sentence
        builder.append(".")

        return builder.toString()
    }

    protected fun referToByName(namedEntity: NamedEntity): NounPhrase {
        val name = if (narrativeContext.isKnownEntity(namedEntity)) namedEntity.getName().definite()
                else namedEntity.getName().indefinite()

        narrativeContext.addKnownEntity(namedEntity)
        return name
    }
}