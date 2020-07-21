package cga.framework

import org.joml.*

data class AnimationVertex (
        var position: Vector3f = Vector3f(),
        var texCoord: Vector2f = Vector2f(),
        var normal: Vector3f = Vector3f(),
        var index: Vector4i = Vector4i(),
        var weight: Vector4f = Vector4f()
) {
}