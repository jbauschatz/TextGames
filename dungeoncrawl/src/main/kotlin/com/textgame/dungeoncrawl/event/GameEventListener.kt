package com.textgame.dungeoncrawl.event

interface GameEventListener {

    fun handleEvent(event: GameEvent)
}