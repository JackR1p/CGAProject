package cga.framework

import org.joml.Matrix4f
import org.joml.Vector3f

class Bone(
        var id: Int = 0,
        var name: String = "null",
        var children: MutableList<Bone> = mutableListOf(),
        var offset: Matrix4f = Matrix4f(), // Transformiert die Vertex Position vom local in den "Bone" Space
        var transform: Matrix4f = Matrix4f(), // Transformiert vom Node Space in Parent Space
        var animateMatrix: Matrix4f = Matrix4f(),  // wird berechnet und an den Shader übergeben. Positioniert die Vertices, je nach Gewicht
        var numBones : Int = 0
) {
    fun countBones(node : Bone){
        numBones++
        for(i in node.children){
            countBones(i)
        }
    }
}