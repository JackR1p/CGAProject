package cga.exercise.components.geometry

import cga.framework.GameWindow

interface Updatable {
    fun update(gameWindow: GameWindow, dt : Float, t : Float)
}