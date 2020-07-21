package cga.framework

import org.joml.Matrix4f
import org.lwjgl.assimp.AIMatrix4x4

class Bone(
        var id: Int = 0,
        var name: String = "null",
        var offset: AIMatrix4x4,
        var children : MutableList<Bone> = mutableListOf(),
        var parent : Bone? = null
) {

}