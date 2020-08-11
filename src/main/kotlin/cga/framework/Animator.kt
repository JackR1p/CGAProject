package cga.framework

import cga.exercise.components.geometry.AnimRenderable
import org.joml.Matrix4f

class Animator(
        var cur_Animation: Animation = Animation(),
        var time: Double = 0.0,
        var animations: Map<String, Animation> = mapOf(),
        var model: AnimRenderable? = null
) {

    fun playAnimation(name: String, dt: Float) {
        cur_Animation = animations[name] ?: Animation()
        progressTime(dt)
        val progression = KeyframeProgress(getCurrentKeyframes())
        val currentPose = interpolatePoses(getCurrentKeyframes(), progression)
        applyPose(model!!.meshes[0].rootBone, currentPose, Matrix4f())
    }

    fun playAnimationReverse(name: String, dt: Float) {
        cur_Animation = animations[name] ?: Animation()
        progressTimeReverse(dt)
        val keyframes = getCurrentKeyframes().reversed().toTypedArray()
        val progression = KeyframeProgress(keyframes)
        val currentPose = interpolatePoses(keyframes, progression)
        applyPose(model!!.meshes[0].rootBone, currentPose, Matrix4f())
    }

    private fun progressTimeReverse(dt: Float) : Double {
        time -= dt
        if (time <= 0) {
            time = cur_Animation.durotation
        }
        return time
    }

    private fun progressTime(dt: Float): Double {
        time += dt
        if (time >= cur_Animation.durotation) {
            time = 0.0
        }
        return time
    }

    fun applyPose(node: Bone, pose: Map<String, JointTransform>, parentMatrix: Matrix4f) {

        // animateMatrix => finalResult
        // Die Transformation der Knochen im Weltkoordinatensystem
        // node.offset: transformiert vom node Space in den Ursprung des Meshes
        var curTransform = node.transform
        if (pose[node.name] != null) {
            curTransform = pose[node.name]!!.getTransform()
        }
        val globalTransform = Matrix4f(parentMatrix).mul(curTransform)
        node.animateMatrix = Matrix4f(globalTransform).mul(node.offset)
        for (i in node.children) {
            applyPose(i, pose, globalTransform)
        }
    }


    // Für jedes Keyframe desselben Timestamps
    fun interpolatePoses(keyframes: Array<Keyframe>, progression: Double): Map<String, JointTransform> {
        val current_pose = mutableMapOf<String, JointTransform>()
        for (i in keyframes[0].transformations) {
            val currentTransform =
                    JointTransform.interpolate(keyframes[0].transformations[i.key]!!, keyframes[1].transformations[i.key]!!, progression.toFloat())
            current_pose[i.key] = currentTransform
        }
        return current_pose
    }

    fun getCurrentKeyframes(): Array<Keyframe> {
        val keyframes = cur_Animation.keyframes
        var res = arrayOf<Keyframe>()
        for (i in 0 until cur_Animation.keyframes.size - 1) {
            if (time >= keyframes[i].timestamp && time <= keyframes[i + 1].timestamp) {
                res = arrayOf(keyframes[i], keyframes[i + 1])
                break
            }
        }
        return res
    }

    fun KeyframeProgress(k: Array<Keyframe>): Double {
        val totalTime = k[1].timestamp - k[0].timestamp
        val current_time = time - k[0].timestamp
        return current_time / totalTime
    }
}