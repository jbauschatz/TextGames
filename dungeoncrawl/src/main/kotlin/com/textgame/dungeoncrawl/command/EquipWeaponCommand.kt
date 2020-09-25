package com.textgame.dungeoncrawl.command

import com.textgame.dungeoncrawl.model.creature.Creature
import com.textgame.dungeoncrawl.model.item.Weapon

class EquipWeaponCommand(val actor: Creature, val weapon: Weapon): GameCommand