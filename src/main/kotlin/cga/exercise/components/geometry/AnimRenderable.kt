package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.framework.*
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW

// Mesh => Vertices, Polygone usw.
class AnimRenderable(var meshes: List<AnimationMesh> = listOf(), var shader: ShaderProgram? = null, matrix: Matrix4f = Matrix4f(),
                     parent: Transformable? = null,
                     var animator: Animator = Animator()
) : IRenderable, Transformable(matrix, parent) {

    init {
        animator.model = this
        //val rb = meshes[0].rootBone
        //rb.countBones(rb)
        //print(rb.numBones)
    }

    override fun render(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", false, getWorldModelMatrix())
        for (m in meshes) {
            m.render(shaderProgram)
        }
    }

    // Verhalten des AnimRenderables zur Laufzeit
    fun update(window: GameWindow, dt: Float) {
        if (window.getKeyState(GLFW.GLFW_KEY_W)) {
            animator.playAnimation("Animation0", dt)
        }
    }
}