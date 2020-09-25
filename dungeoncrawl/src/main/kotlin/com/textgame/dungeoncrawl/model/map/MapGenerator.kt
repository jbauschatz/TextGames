package com.textgame.dungeoncrawl.model.map

import com.textgame.dungeoncrawl.english.*
import com.textgame.dungeoncrawl.ifPercent
import com.textgame.dungeoncrawl.model.Container
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Consumable
import com.textgame.dungeoncrawl.model.item.Item
import com.textgame.dungeoncrawl.model.item.Treasure
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

        private class DoorData(
                val name: String,
                val direction: String,
                val from: Location,
                val to: String
        )

        fun generateMap(filename: String): GameMap {
            val map = loadMap(filename)

            // Generate random "encounters" and loot for each room
            map.locations.forEach {
                ifPercent(50) {
                    generateBandit(it)
                }
                ifPercent(33) {
                    generateMonster(
                            Noun("skeever"),
                            THIRD_PERSON_SINGULAR_NEUTER,
                            1,
                            it
                    )
                }
                ifPercent(50) {
                    generateMonster(
                            Noun("skeleton"),
                            THIRD_PERSON_SINGULAR_NEUTER,
                            20,
                            it,
                            listOf(Weapon(nextId(), Adjective("bone", Noun("shiv")), listOf(ATTACK, STAB, SLASH)))
                    )
                }

                ifPercent(10) {
                    it.inventory.add(Treasure(nextId(), Noun("lock pick")))
                    it.inventory.add(Treasure(nextId(), Adjective("gold", Noun("coin"))))
                }

                ifPercent(75) {
                    generateFurniture(it)
                }
            }

            return map
        }

        fun loadMap(filename: String): GameMap {
            val yaml = Yaml()
            val inputStream = MapGenerator::class.java.classLoader.getResourceAsStream(filename)
            val locationsData: List<Map<String, Any>> = yaml.load(inputStream)

            val allDoorData: MutableSet<DoorData> = mutableSetOf()
            val locationsById: MutableMap<String, Location> = mutableMapOf()

            val locations = locationsData.map { locationData ->
                val location = buildLocation(locationData)
                locationsById[locationData["id"] as String] = location

                // Parse the doors, to be connected once all locations are created
                (locationData["doors"] as List<*>).forEach {
                    @Suppress("UNCHECKED_CAST")
                    val doorData = it as Map<String, String>
                    allDoorData.add(DoorData(
                            doorData.getValue("name"),
                            doorData.getValue("direction"),
                            location,
                            doorData.getValue("to")
                    ))
                }

                location
            }

            // Connect the completed Locations to each other
            allDoorData.forEach {
                val name = Noun(it.name)
                val direction = parse(it.direction)
                val destination = locationsById[it.to]!!
                it.from.doors.add(Door(
                        nextId(),
                        name,
                        THIRD_PERSON_SINGULAR_NEUTER,
                        direction,
                        destination
                ))
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

        private fun generateFurniture(location: Location) {
            pick(
                    {
                        val table = Container(
                                nextId(),
                                Adjective("wooden", Noun("table")),
                                listOf("on", "under")
                        )
                        ifPercent(50) {
                            table.getSlot("on")!!.add(humanWeapon())
                        }
                        ifPercent(50) {
                            table.getSlot("on")!!.add(treasure())
                        }
                        ifPercent(25) {
                            table.getSlot("on")!!.add(potionItem())
                        }
                        ifPercent(10) {
                            table.getSlot("on")!!.add(treasure())
                        }
                        ifPercent(25) {
                            table.getSlot("under")!!.add(treasure())
                        }

                        location.containers.add(table)
                    }
            )
        }

        fun humanWeapon(): Weapon =
                pick(
                        { Weapon(nextId(), Adjective("war", Noun("axe")), listOf(ATTACK, HEW)) },
                        { Weapon(nextId(), Noun("warhammer"), listOf(ATTACK, BLUDGEON)) },
                        { Weapon(nextId(), Adjective("iron", Noun("sword")), listOf(ATTACK, SLASH, STAB)) },
                        { Weapon(nextId(), Adjective("hunting", Noun("bow")), listOf(ATTACK)) }
                )

        fun potionItem() =
                Consumable(
                        nextId(),
                        Adjective("healing", Noun("potion")),
                        THIRD_PERSON_SINGULAR_NEUTER,
                        Verb("drinks", "drink"),
                        10
                )

        fun treasure() =
                pick(
                        { Treasure(nextId(), Adjective("gold", Noun("coin"))) },
                        { Treasure(nextId(), Adjective("silver", Noun("coin"))) },
                        { Treasure(nextId(), Adjective("iron", Noun("coin"))) }
                )
    }
}