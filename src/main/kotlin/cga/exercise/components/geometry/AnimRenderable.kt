package cga.exercise.components.geometry

import cga.exercise.components.collision.Collidable
import cga.exercise.components.collision.Collider
import cga.exercise.components.shader.ShaderProgram
import cga.framework.*
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

// Mesh => Vertices, Polygone usw.
open class AnimRenderable(var meshes: List<AnimationMesh> = listOf(), var shader: ShaderProgram? = null, matrix: Matrix4f = Matrix4f(),
                          parent: Transformable? = null,
                          var animator: Animator = Animator(),
                          collider: Collider = Collider()
) : IRenderable, Collidable, Updatable, Transformable(matrix, parent, collider = collider) {

    constructor(ar : AnimRenderable) : this(ar.meshes, ar.shader, ar.matrix, ar.parent, ar.animator, ar.collider)

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

    }

    override fun onCollide(obj: Transformable, direction: Vector3f) {

    }

}