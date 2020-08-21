package cga.exercise.components.SceneControl

import cga.exercise.components.camera.Camera
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.geometry.Updatable
import cga.exercise.components.light.Lighting
import cga.exercise.components.shader.ShaderProgram
import cga.framework.GameWindow
import org.joml.AABBf
import org.joml.Vector3f

class SceneControl(
        val transformables: MutableList<Transformable> = mutableListOf(),
        val updatable: MutableList<Updatable> = mutableListOf(),
        val lighting: Lighting = Lighting(),
        val camera: List<Camera> = mutableListOf()
) {

    fun update(gameWindow: GameWindow, dt: Float, t: Float) {
        for (i in updatable) {
            i.update(gameWindow, dt, t)
        }
    }

    fun renderCollisionBoxes(shaderProgram: ShaderProgram) {
        for (i in transformables) {
            i.collider.render(i.matrix, shaderProgram)
        }
    }

    fun collision() {
        for (i in transformables) {

            // Originale AABBf muss beibehalten werden, da man der JOML AABBf bloß eine Transformationsmatrix übergeben kann
            if (i.collider.aabbf != null) {
                i.collider.cur_aabbf = AABBf(i.collider.aabbf!!).translate(i.getWorldPosition())
                for (j in transformables) {
                    if (i == j) {
                        continue
                    }
                    j.collider.cur_aabbf = AABBf(j.collider.aabbf!!).translate(j.getWorldPosition())
                    if (i.collider.cur_aabbf!!.testAABB(j.collider.cur_aabbf)) {
                        val rightMidPoint = (Vector3f(j.collider.cur_aabbf!!.maxX, j.collider.cur_aabbf!!.maxY, j.collider.cur_aabbf!!.maxZ)
                                .add(Vector3f(j.collider.cur_aabbf!!.minX, j.collider.cur_aabbf!!.minY, j.collider.cur_aabbf!!.minZ))).div(2f)
                        val leftMidPoint = (Vector3f(i.collider.cur_aabbf!!.maxX, i.collider.cur_aabbf!!.maxY, i.collider.cur_aabbf!!.maxZ)
                                .add(Vector3f(i.collider.cur_aabbf!!.minX, i.collider.cur_aabbf!!.minY, i.collider.cur_aabbf!!.minZ))).div(2f)
                        val direction = leftMidPoint.sub(rightMidPoint)
                        i.collider.onCollide(j, direction)
                    }
                }
            }
        }
    }

    fun initialize() {
        lighting.initializeLights()
        for (i in transformables) {
            i.collider.initializeForm(i)
        }
    }


}