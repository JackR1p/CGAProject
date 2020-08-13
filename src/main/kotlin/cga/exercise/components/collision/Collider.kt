package cga.exercise.components.collision

import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.VertexAttribute
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11.GL_FLOAT
import java.lang.Double.*

class Collider(
        var type: Int = 0, // 0 => Cube, 1 => Sphere, 3 => Ellipsoid
        var form: List<Vector3f> = listOf(), // mit model Matrix der Objekte multiplizieren, damit die Hitbox sich mittransformiert
        var position: Array<Vector3f> = arrayOf(), // beim laden des Objektes mit der rot Matrix multipliziert
        var renderedMesh: List<Vector3f> = listOf()
) {
    fun initializeForm(type : Int){
        // TODO: unterscheidung nach Form switch case
        CFGeneration.setQuickhull(position.toList())
        //Collision.setCollisionCube(position.toList())

    }

    fun Collision(model_matrix: Matrix4f) {
        form = form.map {
            val res = Vector4f(it.x, it.y, it.z, 0.0f).mul(model_matrix)
            Vector3f(res.x, res.y, res.z)
        }
    }

    fun update(model_matrix: Matrix4f) {

        val res = mutableListOf<Vector3f>()
        for (i in form) {
            val res_i = Vector4f(i, 1.0f).mul(model_matrix)
            res.add(Vector3f(res_i.x, res_i.y, res_i.z))
        }

        //res.forEach { print(it) }
        renderedMesh = res
    }

    // Debug
    // TODO: automatisieren sodass es nicht jeden render call aufgerufen werden muss
    fun toMesh(): Mesh {
        val input = renderedMesh
        val floatArray = FloatArray(input.size * 3)
        val intArray = IntArray(input.size * 3)

        for (i in input.indices) {

            floatArray[i * 3] = input[i].x
            floatArray[i * 3 + 1] = input[i].y
            floatArray[i * 3 + 2] = input[i].z

            intArray[i * 3] = i
            intArray[i * 3 + 1] = (i % 7) + 1
            intArray[i * 3 + 2] = (i % 6) + 2

        }
        val vA = arrayOf(VertexAttribute(3, GL_FLOAT, 12, 0))
        return Mesh(floatArray, intArray, vA)
    }

}