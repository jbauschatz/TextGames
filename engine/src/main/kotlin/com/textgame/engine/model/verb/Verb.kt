package com.textgame.engine.model.verb

import com.textgame.engine.model.Person

data class Verb(

        /**
         * ie he/she/it runs
         */
        val presentSingularThirdPerson: String,

        /**
         * ie they/you/I run
         */
        val presentOtherPersons: String
) {

    fun conjugate(person: Person, tense: Tense): String {
        if (tense != Tense.SIMPLE_PRESENT)
            throw NotImplementedError("Only Simple Present conjugation is currently supported")

        if (person == Person.THIRD)
            return presentSingularThirdPerson;

        return presentOtherPersons;
    }
}