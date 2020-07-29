package cga.framework

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

class JointTransform(var timestamp: Double = 0.0, var node: String = "",
                     var position: Vector3f = Vector3f(), var rotation: Quaternionf = Quaternionf()) {

    companion object {
        fun interpolate(j1 : JointTransform, j2 : JointTransform, progress : Float) : JointTransform{
            var res : JointTransform = JointTransform()

            res.position = j1.position.mul(progress).add(j2.position.mul(1-progress))
            res.rotation = j1.rotation.slerp(j2.rotation, progress)
            return res
        }
    }
}