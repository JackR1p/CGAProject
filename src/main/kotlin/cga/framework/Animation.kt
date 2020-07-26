package cga.framework

class Animation(
        var name : String = "",
        var durotation : Double = 0.0,
        var ticksPerSecond : Double = 0.0,
        var keyframes : MutableList<Keyframe> = mutableListOf(),
        delta : Double = 0.0
)
{
    fun play(){

    }
}