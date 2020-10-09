# Introduction
This is a framework for producing narration in a text-based adventure game (or other work of interactive fiction). The project aims to produce high quality narration, with a hand-written feel, programmatically from the smallest building blocks possible. Narration for each round of combat (or other game events) should feel more like a hand-written paragraph than a series of notifications in an event log.

## Running
To play the example game, run:

```shell script
./gradlew playGame
```

To generate a session of ai-driven gameplay, run:

```shell script
./gradlew aiPlay
```

# Overview of the Narration Process
This is a bottom-up process of narrating gameplay. It starts with the smallest building blocks, discrete events that happen within the game. These become sentences, which are formed into more complicated sentences, and eventually coherent paragraphs of storytelling.

#### 1. Generate Game Events
The game engine processes the turn-based gameplay, and produces discrete events that describe all the state changes that occur.
```
[
    MoveEvent(lydia, room1),
    EquipItemEvent(lydia, axe),
    AttackEvent(lydia, bandit, lethal=true)
]
```
#### 2. Convert Events to Sentences
Each Event gets directly converted to a SimpleSentence. These sentences are just an abstract sentence tree (AST), so names and punctuation haven't been determined yet.
```kotlin
[
    SimpleSentence(lydia, VerbPredicate(Verb("enters"), Preposition("from the north"))),
    SimpleSentence(lydia, VerbPredicate(Verb("draws", axe))),
    SimpleSentence(lydia, VerbPredicate(Verb("attacks"), bandit, Preposition("with", axe))),
    SimpleSentence(lydia, VerbPredicate(Verb("kills"), bandit))
]
```
#### 3. Transform Sentences
These sentences are valid English, but they aren't interesting or well written as is. Adjacent sentences that are grammatically compatible can be combined to form fewer, more complicated sentences.
```kotlin
[
    SimpleSentence(lydia, Predicates([
            VerbPredicate(Verb("enters"), Preposition("from the north")),
            VerbPredicate(Verb("draws", axe))
    ])),
    SimpleSentence(lydia, Predicates([
            VerbPredicate(Verb("attacks"), bandit, Preposition("with", axe)),
            VerbPredicate(Verb("kills"), bandit)
    ]))
]
```

#### 4. Realize Sentences
Realization is the process of converting this AST into the final readable form. This is the most difficult part, as it requires being meticulous about pronouns and names to avoid ambiguity.
```
[
    "Lydia enters from the north and draws her axe.",
    "She attacks the bandit with her axe and kills him."
]
```
# Sentence Realization
#### Definite vs Indefinite Articles
English uses definite articles for nouns that are unique or unambigious given the context. For example "the tree" refers to some specific tree which the reader can infer. "A tree" refers to a tree that was not previously specified.

**Bad narration:**
```
You see a skeleton. A skeleton attacks you.
```
The above reads awkwardly because when "a skeleton" attacks you, it's not clear that it's the same skeleton you just saw.

**Good narration:**
```
You see a skeleton. The skeleton attacks you.
```
This version makes more sense, because the second time it refers to the skeleton it's already a familiar entity. This is a small touch that really improves the tone of narration.

#### Pronouns
Pronouns might be the trickiest thing to get right. If pronouns aren't used carefully, a "Who's on First" scenario is pretty likely.

```
You see a skeleton. It attacks you.
```

In this simple example, "it" can only refer to the skeleton and so the skeleton should be referenced by this pronoun.

```
You see a robed cultist. He draws his kife and attacks you with it.
```
This example is more complex because both "he" and "it" are used. The usage of both is clear, because "he" must refer to the cultist while "it" refers to his knife.

#### Pragmatic Naming
Both of the above issues relate to naming things, and ultimately it's a problem of referring to entities in the most concise and unambiguous way, while still feeling like natural English.

An object named "ancient golden sword" could also be referred to as "ancient sword", "golden sword", or just "sword". Any of these may or may not be ambiguous, and the game should generally try to use the shortest unambiguous name in order to sound natural.

```
You see a rusty sword and an ancient golden sword.

> Take sword
There are multiple items by that name. Try being more specific.

> Take ancient sword
You take the ancient golden sword
```
