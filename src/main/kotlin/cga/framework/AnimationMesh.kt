package cga.framework

import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AnimationMesh(
        var vertices: ByteBuffer,
        var indices: IntArray,
        var attributes: Array<VertexAttribute>,
        var material: Material? = null,
        var rootBone : Bone = Bone()
) {
    private var vao = 0
    private var vbo = 0
    private var ibo = 0
    private var indexcount : Int

    init {

        vao = GL30.glGenVertexArrays()
        vbo = GL15.glGenBuffers()
        ibo = GL15.glGenBuffers()

        GL30.glBindVertexArray(vao)
        GL15.glBindBuffer(GL_ARRAY_BUFFER, vbo)
        GL15.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo)

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices.flip(), GL15.GL_STATIC_DRAW)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW)

        for (x in attributes.indices) {
            if(attributes[x].type != GL11.GL_UNSIGNED_INT){
                GL20.glEnableVertexAttribArray(x)
                GL30.glVertexAttribPointer(x, attributes[x].n, attributes[x].type, false,
                        attributes[x].stride, attributes[x].offset.toLong())
            } else {
                GL20.glEnableVertexAttribArray(x)
                GL30.glVertexAttribIPointer(x, attributes[x].n, attributes[x].type,
                        attributes[x].stride, attributes[x].offset.toLong())
            }
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)
        indexcount = indices.size
    }

    fun render() {
        GL30.glBindVertexArray(vao)
        GL15.glDrawElements(GL15.GL_TRIANGLES, indexcount, GL15.GL_UNSIGNED_INT, 0)
        GL30.glBindVertexArray(0)
    }

    fun render(shaderProgram: ShaderProgram){
        material?.bind(shaderProgram)
        bindBones(rootBone, shaderProgram)
        render()
    }

    private fun bindBones(root : Bone, shaderProgram: ShaderProgram){
        shaderProgram.setUniform("Bones[${root.id}]", false, root.animateMatrix)
        for(i in root.children){
            bindBones(i, shaderProgram)
        }
    }

    fun cleanup() {
        if (ibo != 0) GL15.glDeleteBuffers(ibo)
        if (vbo != 0) GL15.glDeleteBuffers(vbo)
        if (vao != 0) GL30.glDeleteVertexArrays(vao)
    }
}