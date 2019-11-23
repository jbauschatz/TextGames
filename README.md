# Project Overview
This is a framework for producing narration in a text-based adventure game (or other work of interactive fiction). The project aims to produce high quality narration, with a hand-written feel, programmatically from the smallest building blocks possible. Narration for each round of combat (or other game events) should feel more like a hand-written paragraph than a series of notifications in an event log.

To accomplish this, the framework includes datastructure to represent English sentences, as well as a system for transforming game events into basic sentences. The engine's embedded grammatical rules act on these simple sentences to create a more complicated narrative, ideally with complex emergent behavior.

### Complex sentences
Many text-based games narrate individual events in a rapid-fire fashion. Example:

```
You go north. You see a skeleton. The skeleton draws a knife. The skeleton attacks you.
```

These sentences are simple, consisting only of Subject/Verb/Object, and are not engaging to read. These sentences can be programmatically combined into larger sentences by following some basic grammar rules. Example:
```
You go north, and see a skeleton. The skeleton draws its knife and attacks you.
```
By applying a few simple grammar rules, we can produce narration which seems more hand-written than the typical punchy sequence of short sentences.

### Definite vs Indefinite Articles
English uses definite articles for nouns that are unique or unambigious given the context. For example "the tree" refers to some specific tree which the reader can infer. "A tree" refers to a tree that was not previously specified.

**Bad narration:**
```
You see a skeleton.

A skeleton attacks you.
```
The above narration reads awkwardly because when "a skeleton" attacks you, it's not precisely indicated that it's the same skeleton you just saw.

**Good narration:**
```
You see a skeleton.

The skeleton attacks you.
```
This narration makes sense, because the skeleton is first introduced as "a skeleton" but subsequently referred to as "the skeleton".

### Pronouns
Pronouns are a shorthand way to refer to known entities. They can only be used when their meaning is unambiguous, so they must be avoided unless circumstances are correct.

```
You see a skeleton.

It attacks you.
```

In this simple example, "it" can only refer to the skeleton and so the skeleton should be referenced by this pronoun.

```
You see a robed cultist. He draws his kife and attacks you with it.
```
This example is more complex because both "he" and "it" are used. The usage of both is clear, because "he" must refer to the cultist while "it" refers to his knife.

### Pragmatic Naming
Any given object can be reffered to by many names, each of which may be unique or not given the present context. A game should describe objects, and interpret user commands, based on which names are unambiguous.

```
You see a rusty sword and an ancient golden sword.

> Take sword
There are multiple items by that name. Try being more specific.

> Take ancient sword
You take the ancient golden sword
```
