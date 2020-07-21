package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

open class PointLight(position: Vector3f, var color: Vector3f, parent: Transformable,
                      var intensity: Float = 1f,
                      var constantAttenuation: Float = 1f,
                      var linearAttenuation: Float = 0.5f,
                      var quadraticAttenuation: Float = 0.01f) : Transformable(Matrix4f(), parent), IPointLight {

    init {
        matrix.translate(position)
    }

    override fun bind(shaderProgram: ShaderProgram, name: String) {
        shaderProgram.setUniform(name + "_lightPosition", getWorldPosition())
        shaderProgram.setUniform(name + "_lightColor", color)
        shaderProgram.setUniform(name + "_intensity", intensity)
        shaderProgram.setUniform(name + "_constantAttenuation", constantAttenuation)
        shaderProgram.setUniform(name + "_linearAttenuation", linearAttenuation)
        shaderProgram.setUniform(name + "_quadraticAttenuation", quadraticAttenuation)
    }
}