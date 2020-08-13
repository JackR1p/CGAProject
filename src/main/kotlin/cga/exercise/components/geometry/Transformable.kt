package cga.exercise.components.geometry

import cga.exercise.components.collision.Collider
import org.joml.Matrix4f
import org.joml.Vector3f
import java.util.*

open class Transformable(var matrix: Matrix4f, var parent: Transformable?, var collider: Collider = Collider(),
                         var name: String = "t") : ITransformable {


    override fun rotateLocal(pitch: Float, yaw: Float, roll: Float) {
        matrix.rotateXYZ(pitch, yaw, roll)
    }

    override fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        val tmp = Matrix4f()
        tmp.translate(altMidpoint)
        tmp.rotateXYZ(pitch, yaw, roll)
        tmp.translate(Vector3f(altMidpoint).negate())
        matrix = tmp.mul(matrix)
    }

    override fun translateLocal(deltaPos: Vector3f) {
        matrix.translate(deltaPos)
    }

    override fun translateGlobal(deltaPos: Vector3f) {
        matrix = (Matrix4f().translate(deltaPos)).mul(matrix)
    }

    override fun scaleLocal(scale: Vector3f) {
        matrix.scale(scale)
    }

    override fun getPosition(): Vector3f {
        return Vector3f(matrix.m30(), matrix.m31(), matrix.m32())
    }

    override fun getWorldPosition(): Vector3f {
        val tmp = getWorldModelMatrix()
        return Vector3f(tmp.m30(), tmp.m31(), tmp.m32())
    }

    override fun getXAxis(): Vector3f {
        return Vector3f(matrix.m00(), matrix.m01(), matrix.m02()).normalize()
    }

    override fun getYAxis(): Vector3f {
        return Vector3f(matrix.m10(), matrix.m11(), matrix.m12()).normalize()

    }

    override fun getZAxis(): Vector3f {
        return Vector3f(matrix.m20(), matrix.m21(), matrix.m22()).normalize()
    }

    override fun getWorldXAxis(): Vector3f {
        val tmp = getWorldModelMatrix()
        return Vector3f(tmp.m00(), tmp.m01(), tmp.m02()).normalize()
    }

    override fun getWorldYAxis(): Vector3f {
        val tmp = getWorldModelMatrix()
        return Vector3f(tmp.m10(), tmp.m11(), tmp.m12()).normalize()
    }

    override fun getWorldZAxis(): Vector3f {
        val tmp = getWorldModelMatrix()
        return Vector3f(tmp.m20(), tmp.m21(), tmp.m22()).normalize()
    }

    override fun getWorldModelMatrix(): Matrix4f {
        val tmp = Matrix4f(matrix)
        if (parent != null)
            parent?.getWorldModelMatrix()?.mul(matrix, tmp)
        return tmp
    }

    override fun getLocalModelMatrix(): Matrix4f {
        return matrix
    }

}