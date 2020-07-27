package cga.framework

class Keyframe(
        var timestamp : Double = 0.0,
        var transformations : Map<String, JointTransform> = mutableMapOf()
) {
    fun setAnimationMatrix(){
        
    }
}