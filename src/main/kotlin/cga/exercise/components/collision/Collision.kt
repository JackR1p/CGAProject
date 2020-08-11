package cga.exercise.components.collision

import org.joml.Vector3f

object Collision {

    fun setCollisionCube(list: List<Vector3f>): List<Vector3f> {
        val res = arrayListOf<Vector3f>()
        var max_x = Double.NEGATIVE_INFINITY.toFloat()
        var max_y = Double.NEGATIVE_INFINITY.toFloat()
        var max_z = Double.NEGATIVE_INFINITY.toFloat()
        var min_x = Double.POSITIVE_INFINITY.toFloat()
        var min_y = Double.POSITIVE_INFINITY.toFloat()
        var min_z = Double.POSITIVE_INFINITY.toFloat()

        for (i in list.indices) {
            if (max_x < list[i].x) {
                max_x = list[i].x
            }
            if (max_y < list[i].y) {
                max_y = list[i].y
            }
            if (max_z < list[i].z) {
                max_z = list[i].z
            }
            if (min_x > list[i].x) {
                min_x = list[i].x
            }
            if (min_y > list[i].y) {
                min_y = list[i].y
            }
            if (min_z > list[i].z) {
                min_z = list[i].z
            }
        }

        res.add(Vector3f(min_x, min_y, min_z)) // 1
        res.add(Vector3f(min_x, max_y, min_z)) // 2
        res.add(Vector3f(min_x, min_y, max_z)) // 3
        res.add(Vector3f(min_x, max_y, max_z)) // 4
        res.add(Vector3f(max_x, min_y, min_z)) // 5
        res.add(Vector3f(max_x, max_y, min_z)) // 6
        res.add(Vector3f(max_x, min_y, max_z)) // 7
        res.add(Vector3f(max_x, max_y, max_z)) // 8

        return res
    }

    fun setCollisionSphere(list : List<Vector3f>) {
        val height: Float
    }

    fun SetQuickhull(list : List<Vector3f>) {
        // find 4 points which define a maximal volume Tetrahedron => Cube
        // => alle unterschiedliche Punkte der Menge an Vertices erfüllen das Kriterium
        // beliebige Punkte in form (SetCollisionCube)
        val cube = setCollisionCube(list)
        val max_tetrahedron = listOf(cube[0], cube[1], cube[3])
        val outside = mutableListOf<Vector3f>()

        // Wenn min und max auf einer Ebene liegen, ist das Objekt zweidimensional und kann keine Quickhull haben
        for (i in max_tetrahedron) {
            for (j in max_tetrahedron) {
                if (i == j) {
                    return
                }
            }
        }



    }

}