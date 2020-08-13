package cga.exercise.components.geometry

import cga.exercise.components.collision.Collidable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

// Mesh => Vertices, Polygone usw.
class Renderable(var meshes: List<Mesh> = listOf(), matrix: Matrix4f = Matrix4f(), parent: Transformable? = null)
    : IRenderable, Collidable, Transformable(matrix = matrix, parent = parent) {

    init {
        collider.model = this
    }

    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", false, getWorldModelMatrix())
        for (m in meshes) {
            m.render(shaderProgram)
        }
    }

    fun update() {

    }

    override fun onCollide(normal: Vector3f, obj: Transformable) {
    }
}