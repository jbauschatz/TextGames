package com.textgame.dungeoncrawl.model.map

import com.textgame.dungeoncrawl.ifPercent
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Consumable
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.item.Weapon
import com.textgame.dungeoncrawl.model.map.CardinalDirection.Companion.parse
import com.textgame.dungeoncrawl.pick
import com.textgame.dungeoncrawl.strategy.MonsterStrategy
import com.textgame.engine.model.NamedEntity.Companion.nextId
import com.textgame.engine.model.nounphrase.*
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_FEMININE
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_MASCULINE
import com.textgame.engine.model.nounphrase.Pronouns.Companion.THIRD_PERSON_SINGULAR_NEUTER
import com.textgame.engine.model.verb.Verb
import org.yaml.snakeyaml.Yaml

class MapGenerator {

    companion object {

        private class Connection(
                val direction: String,
                val from: Location,
                val to: String
        )

        fun generateMap(filename: String): GameMap {
            val map = loadMap(filename)

            // Generate random "encounters" and loot for each room
            map.locations.forEach {
                ifPercent(25) {
                    generateBandit(it)
                }
                ifPercent(25) {
                    generateMonster(
                            Noun("skeever"),
                            THIRD_PERSON_SINGULAR_NEUTER,
                            1,
                            it
                    )
                }
                ifPercent(25) {
                    generateMonster(
                            Noun("skeleton"),
                            THIRD_PERSON_SINGULAR_NEUTER,
                            20,
                            it,
                            listOf(Item(nextId(), Adjective("bone", Noun("shiv"))))
                    )
                }

                ifPercent(10) {
                    it.inventory.add(Item(nextId(), Noun("lock pick")))
                    it.inventory.add(Item(nextId(), Adjective("gold", Noun("coin"))))
                }
            }

            return map
        }

        fun loadMap(filename: String): GameMap {
            val yaml = Yaml()
            val inputStream = javaClass.classLoader.getResourceAsStream(filename)
            val locationsData: List<Map<String, Any>> = yaml.load(inputStream)

            val connections: MutableSet<Connection> = mutableSetOf()
            val locationsById: MutableMap<String, Location> = mutableMapOf()

            val locations = locationsData.map {
                val location = buildLocation(it)
                locationsById[it["id"] as String] = location

                // Parse the doors, to be connected once all locations are created
                (it["doors"] as List<*>).forEach {
                    val doorMap = it as Map<String, String>
                    connections.add(Connection(doorMap["direction"]!!, location, doorMap["to"]!!))
                }

                location
            }

            // Connect the completed Locations to each other
            connections.forEach {
                val destination = locationsById[it.to]!!
                val direction = parse(it.direction)
                it.from.doors[direction] = destination
            }

            return GameMap(locations, locations[0])
        }

        private fun buildLocation(locationData: Map<String, Any>): Location {
            return Location(
                    nextId(),
                    ProperNoun(locationData["name"] as String),
                    THIRD_PERSON_SINGULAR_NEUTER,
                    locationData["description"] as String
            )
        }

        private fun generateMonster(name: NounPhrase, pronouns: Pronouns, maxHealth:Int, location: Location, equipment: List<Item> = listOf()) {
            val enemy = Creature(
                    nextId(),
                    name,
                    pronouns,
                    maxHealth,
                    location,
                    MonsterStrategy
            )
            equipment.forEach { enemy.addItem(it) }

            enemy.allyGroups.add("MONSTER")
            location.creatures.add(enemy)
        }

        private fun generateBandit(location: Location) {
            val bandit = Creature(
                    nextId(),
                    Noun("bandit"),
                    pick(THIRD_PERSON_SINGULAR_MASCULINE, THIRD_PERSON_SINGULAR_FEMININE),
                    30,
                    location,
                    MonsterStrategy
            )
            bandit.addItem(humanWeapon())

            ifPercent(75) {
                bandit.addItem(potionItem())
            }

            bandit.allyGroups.add("BANDIT")
            location.creatures.add(bandit)
        }

        fun humanWeapon(): Item =
                pick(
                        { Weapon(nextId(), Adjective("war", Noun("axe"))) },
                        { Weapon(nextId(), Noun("warhammer")) },
                        { Weapon(nextId(), Adjective("iron", Noun("sword"))) },
                        { Weapon(nextId(), Adjective("hunting", Noun("bow"))) }
                )

        fun potionItem() =
                Consumable(
                        nextId(),
                        Adjective("healing", Noun("potion")),
                        THIRD_PERSON_SINGULAR_NEUTER,
                        Verb("drinks", "drink"),
                        10
                )
    }
}