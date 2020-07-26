package cga.framework

import cga.exercise.components.geometry.Transformable
import org.joml.Matrix4f
import org.lwjgl.assimp.AIMatrix4x4

class Bone(
        var id: Int = 0,
        var name: String = "null",
        var children: MutableList<Bone> = mutableListOf(),
        var offset: Matrix4f = Matrix4f(), // Transformiert die Vertex Position vom local in den "Bone" Space
        var transform: Matrix4f = Matrix4f(), // Transformiert vom Node Space in Parent Space
        var animateMatrix: Matrix4f = Matrix4f()  // wird berechnet und an den Shader übergeben. Positioniert die Vertices, je nach Gewicht
) {
    fun bind() {

    }
}