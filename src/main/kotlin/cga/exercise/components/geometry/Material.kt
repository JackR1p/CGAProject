package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f

class Material(var diff: Texture2D? = null,
               var emit: Texture2D? = null,
               var specular: Texture2D? = null,
               var shininess: Float = 50.0f,
               var tcMultiplier : Vector2f = Vector2f(1.0f)){

    fun bind(shaderProgram: ShaderProgram) {
        if(diff != null){
            shaderProgram.setUniform("diff", 0)
            diff!!.bind(0)
        }
        if(emit != null){
            shaderProgram.setUniform("emit", 1)
            emit?.bind(1)
        }
        if(specular != null){
            shaderProgram.setUniform("specular", 2)
            specular?.bind(2)
        }
        shaderProgram.setUniform("tcMultiplier", tcMultiplier)
        shaderProgram.setUniform("shininess", shininess)
    }
}