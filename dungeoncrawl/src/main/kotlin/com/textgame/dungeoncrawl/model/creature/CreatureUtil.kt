import com.textgame.dungeoncrawl.model.creature.ActionType
import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.map.Location

fun Creature.isDead() = this.health <= 0

fun Creature.takeDamage(damage: Int) {
    this.health = Math.max(0, this.health - damage)
}

fun Creature.heal(healing: Int) {
    this.health = Math.min(this.health + healing, this.maxHealth)
}

/**
 * Gets the living enemies of the [Creature] in its current [Location]
 */
fun Creature.enemies(): List<Creature> =
        this.location.creatures.members()
                .filter { it != this && !it.isDead() }

/**
 * Resets a [Creature]'s pool of available actions to correct state for the beginning of turn
 */
fun Creature.resetActions() {
    this.actionsAvailable.clear()
    this.actionsAvailable[ActionType.ATTACK] = 1
    this.actionsAvailable[ActionType.MOVE] = 1
}

fun Creature.spendAction(actionType: ActionType) {
    this.actionsAvailable[actionType] = this.actionsAvailable[actionType]!! - 1;
}

fun Creature.spendAllActions() {
    this.actionsAvailable[ActionType.ATTACK] = 0
    this.actionsAvailable[ActionType.MOVE] = 0
}

fun Creature.hasMoreActions(): Boolean {
    return hasActionAvailable(ActionType.ATTACK)
            || hasActionAvailable(ActionType.MOVE)
}

fun Creature.hasActionAvailable(actionType: ActionType): Boolean =
        this.actionsAvailable[actionType]!! >= 1