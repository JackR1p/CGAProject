package cga.framework

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

class JointTransform(var timestamp: Double = 0.0, var node: String = "",
                     var position: Vector3f = Vector3f(), var rotation: Quaternionf = Quaternionf()) {

    companion object {

        // korrekt
        fun interpolate(j1: JointTransform, j2: JointTransform, progress: Float): JointTransform {
            if (j1.position == j2.position && j1.rotation == j2.rotation) {
                return j1
            }
            val res = JointTransform()
            res.position = Vector3f(
                    j1.position.x * (1 - progress) + j2.position.x * progress,
                    j1.position.y * (1 - progress) + j2.position.y * progress,
                    j1.position.z * (1 - progress) + j2.position.z * progress
            )
            res.rotation = Quaternionf(j1.rotation).slerp(j2.rotation, progress)

            //print("\n Res:Pos \n" + res.position + "\n Res:Rot \n" + res.rotation)
            return res
        }
    }

    // korrekt
    fun getTransform(): Matrix4f {
        val res = Matrix4f()
        res.translate(position)
        res.rotate(rotation)
        return res
    }
}