package cga.framework

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

class Animation(
        var name: String = "",
        var durotation: Double = 0.0,
        var ticksPerSecond: Double = 0.0,
        var keyframes: MutableList<Keyframe> = mutableListOf(),
        var time: Double = 0.0
) {

}