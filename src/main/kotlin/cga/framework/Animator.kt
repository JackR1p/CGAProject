package cga.framework

import cga.exercise.components.geometry.AnimRenderable
import cga.exercise.components.shader.ShaderProgram

class Animator(
        var cur_Animation: Animation = Animation(),
        var time: Double = 0.0,
        var animations: Map<String, Animation> = mapOf(),
        var model: AnimRenderable? = null
) {

    fun playAnimation(name: String, dt : Float) {
        cur_Animation = animations[name] ?: Animation()
        calculatePose(progression(dt))

    }

    // Progression between two Keyframes
    fun progression(dt: Float) : Float{
        time += dt
        if(time >= cur_Animation.durotation){
            time = 0.0
        }
        val res : Double = time / cur_Animation.durotation
        return res.toFloat()
    }

    fun calculatePose(progression : Float){
        cur_Animation.keyframes

    }

}