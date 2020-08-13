package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL20.GL_FLOAT
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20C.glEnableVertexAttribArray


class LineMesh(var vertexdata: FloatArray, indexdata: IntArray) {
    private var vao = 0
    private var vbo = 0
    private var ibo = 0
    var indexcount: Int

    init {

        vao = GL30.glGenVertexArrays()
        vbo = GL15.glGenBuffers()
        ibo = GL15.glGenBuffers()

        GL30.glBindVertexArray(vao)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo)

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexdata, GL15.GL_STATIC_DRAW)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexdata, GL15.GL_STATIC_DRAW)

        GL20.glEnableVertexAttribArray(0)
        GL30.glVertexAttribPointer(0, 3, GL_FLOAT, false,
                12, 0)

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)

        indexcount = indexdata.size
    }

    /**
     * renders the mesh
     */
    fun render() {
        GL30.glBindVertexArray(vao)
        GL15.glDrawElements(GL15.GL_LINES, indexcount, GL15.GL_UNSIGNED_INT, 0)
        GL30.glBindVertexArray(0)
    }

    /**
     * Deletes the previously allocated OpenGL objects for this mesh
     */
    fun cleanup() {
        if (ibo != 0) GL15.glDeleteBuffers(ibo)
        if (vbo != 0) GL15.glDeleteBuffers(vbo)
        if (vao != 0) GL30.glDeleteVertexArrays(vao)
    }
}