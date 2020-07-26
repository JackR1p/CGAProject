package cga.framework

import org.joml.Quaternionf
import org.joml.Vector3f

class JointTransform(var timestamp: Double, var node: String = "",
                     var position: Vector3f = Vector3f(), var rotation: Quaternionf = Quaternionf()) {

    init {

    }

}