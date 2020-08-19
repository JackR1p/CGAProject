package cga.exercise.components.gObjects

import cga.exercise.components.geometry.AnimRenderable
import cga.exercise.components.geometry.Transformable
import cga.framework.GameWindow
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

class Player(ar: AnimRenderable) : AnimRenderable(ar) {

    override fun update(gameWindow: GameWindow, dt: Float, t: Float) {

        if (gameWindow.getKeyState(GLFW.GLFW_KEY_W)) {
            translateLocal(Vector3f(0f, 0f, 0.1f))
            animator.playAnimation("Animation0", dt)
        }

        if (gameWindow.getKeyState(GLFW.GLFW_KEY_A)) {
            rotateLocal(0f, Math.toRadians(90.0).toFloat() * dt, 0f)
        }

        if (gameWindow.getKeyState(GLFW.GLFW_KEY_D)) {
            rotateLocal(0f, Math.toRadians(-90.0).toFloat() * dt, 0f)
        }

        if (gameWindow.getKeyState(GLFW.GLFW_KEY_S)) {
            translateLocal(Vector3f(0f, 0f, -0.1f))
            animator.playAnimationReverse("Animation0", dt)
        }
    }

    override fun onCollide(obj: Transformable, direction: Vector3f) {
        matrix.translateLocal(Vector3f(direction.x, 0f, direction.z).mul(0.01f))
    }
}