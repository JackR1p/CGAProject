package cga.exercise.components.collision

import cga.exercise.components.geometry.LineMesh
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.*
import kotlin.properties.ObservableProperty

class Collider(
        var model : Collidable? = null,
        var type: Int = 0, // 0 => Cube, 1 => Sphere
        var position: Array<Vector3f> = arrayOf(), // rohe vertices des Modells,
        var form: MutableList<Vector3f> = mutableListOf(),
        var aabbf: AABBf? = null, // original AABBf, muss vor dem Benutzen mit der Transformable matrix transformiert werden
        var spheref: Spheref? = null,
        var linemesh: LineMesh? = null
) {
    fun initializeForm() {
        // TODO: unterscheidung nach Form
        var vertices: MutableList<Float>
        if (type == 0) {
            vertices = getBoundaries()
            aabbf = AABBf(vertices[0], vertices[1], vertices[2], vertices[3], vertices[4], vertices[5])
            linemesh = LineMesh(getMinMaxVertices(), intArrayOf(
                    0, 1, 2, 3, 4, 5, 6, 7,
                    0, 4, 1, 5, 2, 6, 3, 7,
                    0, 2, 1, 3, 4, 6, 5, 7))
        }
        if (type == 1) {
            val midpoint = getMidpoint(position)
            vertices = getBoundaries()
            val y_comp = vertices[4] - vertices[1]
            spheref = Spheref(midpoint.x, midpoint.y, midpoint.z, (y_comp / 2))
        }

    }

    fun getMidpoint(list: Array<Vector3f>): Vector3f {
        val sum = Vector3f()

        for (i in list) {
            sum.add(i)
        }

        sum.div(list.size.toFloat())
        return sum
    }

    fun getBoundaries(): MutableList<Float> {
        val res = mutableListOf<Float>()
        val vertices = position.toMutableList()
        var max_x = Double.NEGATIVE_INFINITY.toFloat()
        var max_y = Double.NEGATIVE_INFINITY.toFloat()
        var max_z = Double.NEGATIVE_INFINITY.toFloat()
        var min_x = Double.POSITIVE_INFINITY.toFloat()
        var min_y = Double.POSITIVE_INFINITY.toFloat()
        var min_z = Double.POSITIVE_INFINITY.toFloat()

        for (i in vertices.indices) {
            if (max_x < vertices[i].x) {
                max_x = vertices[i].x
            }
            if (max_y < vertices[i].y) {
                max_y = vertices[i].y
            }
            if (max_z < vertices[i].z) {
                max_z = vertices[i].z
            }
            if (min_x > vertices[i].x) {
                min_x = vertices[i].x
            }
            if (min_y > vertices[i].y) {
                min_y = vertices[i].y
            }
            if (min_z > vertices[i].z) {
                min_z = vertices[i].z
            }
        }

        if (min_x == max_x || min_y == max_y || min_z == max_z) {
            return mutableListOf()
        }
        res.addAll(mutableListOf(min_x, min_y, min_z, max_x, max_y, max_z))
        return res
    }

    fun getMinMaxVertices(): FloatArray {
        val vectors = mutableListOf<Vector3f>()
        val res = FloatArray(3 * 8)
        val vertices = position.toMutableList()
        var max_x = Double.NEGATIVE_INFINITY.toFloat()
        var max_y = Double.NEGATIVE_INFINITY.toFloat()
        var max_z = Double.NEGATIVE_INFINITY.toFloat()
        var min_x = Double.POSITIVE_INFINITY.toFloat()
        var min_y = Double.POSITIVE_INFINITY.toFloat()
        var min_z = Double.POSITIVE_INFINITY.toFloat()

        for (i in vertices.indices) {
            if (max_x < vertices[i].x) {
                max_x = vertices[i].x
            }
            if (max_y < vertices[i].y) {
                max_y = vertices[i].y
            }
            if (max_z < vertices[i].z) {
                max_z = vertices[i].z
            }
            if (min_x > vertices[i].x) {
                min_x = vertices[i].x
            }
            if (min_y > vertices[i].y) {
                min_y = vertices[i].y
            }
            if (min_z > vertices[i].z) {
                min_z = vertices[i].z
            }
        }

        vectors.add(Vector3f(min_x, min_y, min_z)) // 1
        vectors.add(Vector3f(min_x, max_y, min_z)) // 2
        vectors.add(Vector3f(min_x, min_y, max_z)) // 3
        vectors.add(Vector3f(min_x, max_y, max_z)) // 4
        vectors.add(Vector3f(max_x, min_y, min_z)) // 5
        vectors.add(Vector3f(max_x, max_y, min_z)) // 6
        vectors.add(Vector3f(max_x, min_y, max_z)) // 7
        vectors.add(Vector3f(max_x, max_y, max_z)) // 8

        form = vectors

        for (i in vectors.indices) {
            res[i * 3] = vectors[i].x
            res[i * 3 + 1] = vectors[i].y
            res[i * 3 + 2] = vectors[i].z
        }

        return res
    }

    fun onCollide(normal : Vector3f, obj : Transformable){
        model!!.onCollide(normal, obj)
    }

    fun render(matrix4f: Matrix4f, shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("model_matrix", false, matrix4f)
        if (linemesh != null) {
            linemesh!!.render()
        }
    }
}