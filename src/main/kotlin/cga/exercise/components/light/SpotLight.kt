package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.cos

class SpotLight(pos: Vector3f, col: Vector3f, parent: Transformable, var innerConeAngle: Float, var outerConeAngle: Float) : ISpotLight, PointLight(pos, col, parent) {

    override fun bind(shaderProgram: ShaderProgram, name: String, viewMatrix: Matrix4f) {
        super.bind(shaderProgram, name)
        shaderProgram.setUniform(name + "_innerConeAngle", cos(Math.toRadians(innerConeAngle.toDouble())).toFloat())
        shaderProgram.setUniform(name + "_outerConeAngle", cos(Math.toRadians(outerConeAngle.toDouble())).toFloat())
        shaderProgram.setUniform(name + "_spotDirection", parent!!.getXAxis().negate())
    }
}