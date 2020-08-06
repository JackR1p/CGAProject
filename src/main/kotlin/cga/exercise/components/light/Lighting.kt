package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import kotlin.math.cos

class Lighting {

    var lightings: MutableList<PointLight> = mutableListOf<PointLight>()
    var pointlights: MutableList<PointLight> = mutableListOf<PointLight>()
    var spotlights: MutableList<SpotLight> = mutableListOf<SpotLight>()
    var count = 0

    fun initializeLights() {
        for (x in lightings) {
            if (x is SpotLight)
                spotlights.add(x)
            else
                pointlights.add(x)
        }
    }

    fun add(p: PointLight) {
        lightings.add(p)
        count++
    }

    fun add(p: List<PointLight>) {
        for (x in p) {
            lightings.add(x)
        }
        count += p.size
    }

    fun empty() {
        lightings = mutableListOf()
        count = 0
    }

    fun show() {
        for (x in lightings) {
            print(x.getPosition())
        }
    }

    fun bind(shader: ShaderProgram) {
        var i = 0

        for (x in pointlights) {
            shader.setUniform("Lights[$i].position", x.getWorldPosition())
            shader.setUniform("Lights[$i].color", x.color)
            shader.setUniform("Lights[$i].intensity", x.intensity)
            shader.setUniform("Lights[$i].c_att", x.constantAttenuation)
            shader.setUniform("Lights[$i].l_att", x.linearAttenuation)
            shader.setUniform("Lights[$i].q_att", x.quadraticAttenuation)
            i++
        }

        for (x in spotlights) {
            shader.setUniform("Lights[$i].position", x.getWorldPosition())
            shader.setUniform("Lights[$i].color", x.color)
            shader.setUniform("Lights[$i].intensity", x.intensity)
            shader.setUniform("Lights[$i].c_att", x.constantAttenuation)
            shader.setUniform("Lights[$i].l_att", x.linearAttenuation)
            shader.setUniform("Lights[$i].q_att", x.quadraticAttenuation)
            shader.setUniform("Lights[$i].inner", cos(Math.toRadians(x.innerConeAngle.toDouble())).toFloat())
            shader.setUniform("Lights[$i].outer", cos(Math.toRadians(x.outerConeAngle.toDouble())).toFloat())
            shader.setUniform("Lights[$i].spot_dir", x.parent!!.getZAxis())
            i++
        }

        i = 0
    }
}