package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20C.glEnableVertexAttribArray

/**
 * Creates a Mesh object from vertexdata, intexdata and a given set of vertex attributes
 *
 * @param vertexdata plain float array of vertex data
 * @param indexdata  index data
 * @param attributes vertex attributes contained in vertex data
 * @throws Exception If the creation of the required OpenGL objects fails, an exception is thrown
 *
 * Created by Fabian on 16.09.2017.
 */
class Mesh(vertexdata: FloatArray, indexdata: IntArray, attributes: Array<VertexAttribute>, private var material : Material? = null) {
    //private data
    private var vao = 0
    private var vbo = 0
    private var ibo = 0
    var indexcount : Int

    init {

        //vertexdata.forEach { print(" " + it) }

        vao = GL30.glGenVertexArrays()
        vbo = GL15.glGenBuffers()
        ibo = GL15.glGenBuffers()

        GL30.glBindVertexArray(vao)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo)

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexdata, GL15.GL_STATIC_DRAW)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexdata, GL15.GL_STATIC_DRAW)

        for (x in attributes.indices) {
            GL20.glEnableVertexAttribArray(x)
            GL30.glVertexAttribPointer(x, attributes[x].n, attributes[x].type, false,
                    attributes[x].stride, attributes[x].offset.toLong())
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)

        indexcount = indexdata.size
    }

    /**
     * renders the mesh
     */
    fun render() {
        GL30.glBindVertexArray(vao)
        GL15.glDrawElements(GL15.GL_TRIANGLES, indexcount, GL15.GL_UNSIGNED_INT, 0)
        GL30.glBindVertexArray(0)
    }

    fun render(shaderProgram: ShaderProgram){
        material?.bind(shaderProgram)
        render()
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