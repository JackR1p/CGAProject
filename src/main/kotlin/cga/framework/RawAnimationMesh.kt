package cga.framework

import org.joml.Vector4i

class RawAnimationMesh(
        var vertices: MutableList<AnimationVertex> = mutableListOf(),
        var bone_indices : MutableList<Vector4i> = mutableListOf(),
        var indices: MutableList<Int> = mutableListOf(),
        var materialIndex: Int = 0,
        var rootBone : Bone = Bone()
)
{
}