package cga.exercise.components.collision

import org.joml.Intersectionf
import org.joml.Vector3f
import kotlin.math.absoluteValue

object CFGeneration {

    class Face(var v1: Vector3f, var v2: Vector3f, var v3: Vector3f, var normal: Vector3f = Vector3f(),
               var opt: MutableList<Vector3f> = mutableListOf(),
               var outside: MutableList<Vector3f> = mutableListOf(),
               var onHull: Boolean = false) {
        // opt => optional vertices defining a plane
        // outside => set of outside vertices

        fun orientFace(midpoint: Vector3f) {
            val vec1 = Vector3f(v1).sub(v2)
            val vec2 = Vector3f(v3).sub(v2)

            val n = Vector3f(vec1).cross(vec2).normalize()
            var res = Vector3f()

            // Bildet einen Vektor aus dem Mittelpunkt des Meshes zu den nächsten Punkt der Ebene.
            // Das Skalaprodukt zwischen der Normale der Ebene und Vektor verrät in welche Richtung die Normale zeigt
            // finClosestPointOnPlane nicht benutzen

            Intersectionf.findClosestPointOnTriangle(v1, v2, v3, midpoint, res)

            //print(res)
            //print(" $v1 $v2 $v3")

            res = Vector3f(res).sub(midpoint).normalize()

            if (res.dot(n) < 0) {
                n.negate()
            }

            normal = n
        }

        // arbeitet auf der unclaimed Liste und verändert sie
        fun addtoOutsideSet(unclaimed: MutableList<Vector3f>) {

            val res = mutableListOf<Vector3f>() // outside Vertices
            val d = -normal.x * v2.x - normal.y * v2.y - normal.z * v2.z

            for (i in unclaimed) {

                val distance = Intersectionf.distancePointPlane(
                        i.x, i.y, i.z,
                        normal.x, normal.y, normal.z, d)

                //print("q $distance $i")

                if (distance > 0) {
                    res.add(i)
                    outside.add(i)
                } else if (distance == 0f && (v1 != i || v2 != i || v3 != i)) {
                    opt.add(i)
                }
            }
            unclaimed.removeAll(res)
        }
    }

    class Edge(var v1: Vector3f, var v2: Vector3f, var onHull: Boolean = false) {

    }



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

        if (min_x == max_x || min_y == max_y || min_z == max_z) {
            return mutableListOf()
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

    // unfertig, wird möglicherweise nicht weitergeführt
    fun setQuickhull(list: List<Vector3f>) {
        // find 4 points which define a maximal volume Tetrahedron => Cube
        // => alle unterschiedliche Punkte der Menge an Vertices erfüllen das Kriterium
        // beliebige Punkte in form (SetCollisionCube)
        val list_copy: MutableList<Vector3f> = list.toMutableList()
        val cube = setCollisionCube(list)
        val maxTetrahedron = createSimplex(cube, list_copy)
        val faces = genFaces(maxTetrahedron)
        val midpoint = getMidpoint(maxTetrahedron)

        val outside = mutableListOf<Vector3f>()

        // Alle Punkte außerhalb des Tetrahedrons
        for (i in faces) {
            i.orientFace(midpoint)
            i.addtoOutsideSet(list_copy)
        }

        //print(list_copy.size)

        val convexHull = mutableListOf<Face>()
        convexHull.addAll(faces)

        var cap = 0 // Debug
        while (faces.any { it.outside.isNotEmpty() } && cap < 1) {

            for (i in faces) {
                if (i.outside.isEmpty()) {
                    continue
                }
                val eyepoint = getMaxDistancePointFromPlane(i, i.outside)
                val horizon = mutableListOf<Edge>()
                calculateHorizon(eyepoint, null, i, horizon, list_copy, faces)
                // Debug
                break
            }
            cap++
        }
    }

    fun getMidpoint(list: List<Vector3f>): Vector3f {
        val sum = Vector3f()

        for (i in list) {
            sum.add(i)
        }

        sum.div(list.size.toFloat())
        return sum
    }

    fun createSimplex(list: List<Vector3f>, vertices: List<Vector3f>): MutableList<Vector3f> {

        val res = mutableListOf<Vector3f>()

        // definieren einer Linie
        val p1 = list[0]
        val p2 = list[1]
        // Max Distant Point max_d to line
        var tmpLine = 0f
        var maxDLine = Vector3f()
        for (i in vertices) {
            val distance = Intersectionf.distancePointLine(i.x, i.y, i.z, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z)
            if (distance > tmpLine) {
                tmpLine = distance
                maxDLine = i
            }
        }
        if (tmpLine == 0f) {
            return mutableListOf()
        }

        res.add(p1)
        res.add(p2)
        res.add(maxDLine)

        // Max Distance to Plane

        var tmpPlane = 0f
        var maxDPlane = Vector3f()
        for (i in vertices) {
            val distance = Intersectionf.distancePointPlane(i.x, i.y, i.z, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z, maxDLine.x, maxDLine.y, maxDLine.z)

            if (distance.absoluteValue > tmpPlane) {
                tmpPlane = distance.absoluteValue
                maxDPlane = i
            }
        }

        if (tmpPlane < 0) {
            res.reverse()
        }
        if (tmpPlane == 0f) {
            return mutableListOf()
        }

        res.add(maxDPlane)

        return res
    }

    fun genFaces(list: List<Vector3f>): List<Face> {
        val res = mutableListOf<Face>()
        for (i in list) {
            for (j in list) {
                for (k in list) {
                    if (i != j && i != k && j != k) {
                        val face = Face(i, j, k)

                        if (!res.any {
                                    (it.v1 == face.v1 || it.v1 == face.v2 || it.v1 == face.v3) &&
                                            (it.v2 == face.v1 || it.v2 == face.v2 || it.v2 == face.v3) &&
                                            (it.v3 == face.v1 || it.v3 == face.v2 || it.v3 == face.v3)
                                }) {
                            res.add(face)
                        }
                    }
                }
            }
        }
        return res
    }

    fun getMaxDistancePointFromPlane(face: Face, list: List<Vector3f>): Vector3f {

        var tmp = 0f
        var point = Vector3f()
        for (i in list) {
            val distance = Intersectionf.distancePointPlane(i.x, i.y, i.z,
                    face.v1.x, face.v1.y, face.v1.z, face.v2.x, face.v2.y, face.v2.z, face.v3.x, face.v3.y, face.v3.z)
            if (distance > tmp) {
                tmp = distance
                point = i
            }
        }

        return point
    }

    // unfertig
    fun calculateHorizon(eye: Vector3f, crossedEdge: Edge?, curFace: Face, horizon: List<Edge>, unclaimed: List<Vector3f>, faces: List<Face>) {
        // alle sichtbaren punkte/konturen
        // if face is not on convex hull
        if (unclaimed.containsAll(listOf(curFace.v1, curFace.v2, curFace.v3))) {
            //crossedEdge!!.onHull = false
            return
        }
        val closestPointOnPlane = Vector3f()
        Intersectionf.findClosestPointOnTriangle(curFace.v1, curFace.v2, curFace.v3, eye, closestPointOnPlane)
        val direction = Vector3f(eye).sub(closestPointOnPlane)
        val epsilon = 0.000000000001f // ??
        // if curFace is visible from EyePoint

        //print(" POINT $eye \n")
        //print(closestPointOnPlane)
        for (i in faces) {
            if (i == curFace) {
                continue
            }
            // if intersect == -1.0 liegt kein Schnittpunkt mit der Ebene vor und der Punkt ist vom Face aus sichtbar
            val intersect = Intersectionf.intersectRayPlane(closestPointOnPlane, direction, i.v1, i.normal, epsilon)
            if (intersect < 0) {

                break
            }
        }
    }

}