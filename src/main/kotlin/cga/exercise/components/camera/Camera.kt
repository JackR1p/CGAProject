package cga.exercise.components.camera

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

class Camera(parent: Transformable? = null, var aspectratio: Float, var fov: Float, var near: Float, var far: Float)
    : ICamera, Transformable(Matrix4f(), parent) {

    override fun getCalculateViewMatrix(): Matrix4f {
        return Matrix4f().lookAt(getWorldPosition(), getWorldPosition().sub(getWorldZAxis()), getWorldYAxis())
    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        return Matrix4f().perspective(fov, aspectratio, near, far)
    }

    override fun bind(shader: ShaderProgram) {
        shader.setUniform("view", false, getCalculateViewMatrix())
        shader.setUniform("proj", false, getCalculateProjectionMatrix())
    }
}