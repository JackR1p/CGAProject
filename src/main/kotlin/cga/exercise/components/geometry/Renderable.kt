package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

// Mesh => Vertices, Polygone usw.
class Renderable(var meshes : List<Mesh> = listOf(), matrix : Matrix4f = Matrix4f(), parent : Transformable? = null) : IRenderable, Transformable(matrix, parent) {

    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", false, getWorldModelMatrix())
        for(m in meshes){
            m.render(shaderProgram)
        }
    }
}