package cga.exercise.components.geometry

import cga.exercise.components.collision.Collidable
import cga.exercise.components.shader.ShaderProgram
import cga.framework.*
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

// Mesh => Vertices, Polygone usw.
class AnimRenderable(var meshes: List<AnimationMesh> = listOf(), var shader: ShaderProgram? = null, matrix: Matrix4f = Matrix4f(),
                     parent: Transformable? = null,
                     var animator: Animator = Animator()
) : IRenderable, Collidable, Updatable, Transformable(matrix, parent) {

    init {
        animator.model = this
        collider.model = this
    }

    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", false, getWorldModelMatrix())
        for (m in meshes) {
            m.render(shaderProgram)
        }
    }

    // Verhalten des AnimRenderables zur Laufzeit
    override fun update(gameWindow: GameWindow, dt: Float, t: Float) {
        if (gameWindow.getKeyState(GLFW.GLFW_KEY_W)) {
            animator.playAnimation("Animation0", dt)
        }
        if (gameWindow.getKeyState(GLFW.GLFW_KEY_S)) {
            animator.playAnimationReverse("Animation0", dt)
        }
    }

    override fun onCollide(obj: Transformable, direction : Vector3f) {
        matrix.translateLocal(Vector3f(direction.x, 0f, direction.z).mul(0.01f))
    }

}