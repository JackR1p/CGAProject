package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.framework.Animation
import cga.framework.AnimationMesh
import cga.framework.Bone
import org.joml.Matrix4f

// Mesh => Vertices, Polygone usw.
class AnimRenderable(var meshes: List<AnimationMesh> = listOf(), matrix: Matrix4f = Matrix4f(),
                     parent: Transformable? = null,
                     var animations: Array<Animation> = arrayOf()) : IRenderable, Transformable(matrix, parent) {


    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", false, getWorldModelMatrix())
        for (m in meshes) {
            m.render(shaderProgram)
        }
    }

    // Verhalten des AnimRenderables zur Laufzeit
    fun update() {

    }

    fun playAnimation(name: String) {
        animations.forEach {
            if (it.name == name) {
                it.play(meshes[0].rootBone)
            }
        }
    }
}