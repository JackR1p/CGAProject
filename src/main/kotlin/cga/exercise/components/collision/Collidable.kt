package cga.exercise.components.collision

import cga.exercise.components.geometry.Transformable
import org.joml.Vector3f

interface Collidable {
    fun onCollide(obj : Transformable, direction : Vector3f)
}