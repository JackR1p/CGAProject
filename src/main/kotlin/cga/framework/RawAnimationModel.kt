package cga.framework

import org.lwjgl.assimp.AIBone

class RawAnimationModel(
        var meshes: MutableList<RawAnimationMesh> = mutableListOf(),
        var materials: MutableList<RawMaterial> = mutableListOf(),
        var textures: MutableList<String> = mutableListOf(),
        var animation: MutableList<Animation> = mutableListOf()
)
{

}