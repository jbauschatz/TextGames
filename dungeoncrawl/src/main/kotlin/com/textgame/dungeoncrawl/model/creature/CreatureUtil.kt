import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.Location

/**
 * Gets the enemies of the [Creature] in its current [Location]
 */
fun Creature.enemies(): List<Creature> =
        this.location.creatures.members()
                .filter { it != this }