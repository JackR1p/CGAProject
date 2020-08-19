package cga.exercise.components.gObjects

import cga.exercise.components.geometry.AnimRenderable
import cga.exercise.components.geometry.Transformable
import cga.framework.GameWindow
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

class Player(ar : AnimRenderable) : AnimRenderable(ar) {

    override fun update(gameWindow: GameWindow, dt : Float, t : Float){
        if (gameWindow.getKeyState(GLFW.GLFW_KEY_W)) {
            animator.playAnimation("Animation0", dt)
        }
        if (gameWindow.getKeyState(GLFW.GLFW_KEY_S)) {
            animator.playAnimationReverse("Animation0", dt)
        }
    }

    override fun onCollide(obj: Transformable, direction: Vector3f) {
        matrix.translateLocal(Vector3f(direction.x, 0f, direction.z).mul(0.01f))
    }
}