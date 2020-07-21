package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f

class Material(var diff: Texture2D,
               var emit: Texture2D,
               var specular: Texture2D,
               var shininess: Float = 50.0f,
               var tcMultiplier : Vector2f = Vector2f(1.0f)){

    fun bind(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("diff", 0)
        diff.bind(0)
        shaderProgram.setUniform("emit", 1)
        emit.bind(1)
        shaderProgram.setUniform("specular", 2)
        specular.bind(2)
        shaderProgram.setUniform("tcMultiplier", tcMultiplier)
        shaderProgram.setUniform("shininess", shininess)
    }
}