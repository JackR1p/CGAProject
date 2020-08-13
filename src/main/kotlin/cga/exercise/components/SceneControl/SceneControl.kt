package cga.exercise.components.SceneControl

import cga.exercise.components.camera.Camera
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.Lighting
import cga.exercise.components.shader.ShaderProgram
import org.joml.AABBf
import org.joml.Vector3f

class SceneControl(
        val transformables: MutableList<Transformable> = mutableListOf(),
        val lighting: Lighting = Lighting(),
        val camera: List<Camera> = mutableListOf()
) {

    fun update(dt : Float, t : Float) {

    }

    fun renderCollisionBoxes(shaderProgram: ShaderProgram) {
        for (i in transformables) {
            i.collider.render(i.matrix, shaderProgram)
        }
    }

    fun testCollision() {
        for (i in transformables) {
            if (i.collider.aabbf == null && i.collider.spheref == null) {
                continue
            }
            // Originale AABBf muss beibehalten werden, da man der JOML AABBf bloß eine Transformationsmatrix übergeben kann
            val leftAABB = AABBf(i.collider.aabbf!!).transform(i.matrix)
            for (j in transformables) {
                if (i == j) {
                    continue
                }
                val rightAABBf = AABBf(j.collider.aabbf!!).transform(j.matrix)
                if (i.collider.aabbf is AABBf && j.collider.aabbf is AABBf) {

                    // wie liefert man Normale?
                    // wenn Collision gefunden, überprüfe auf welcher Plane die Collision stattgefunden hat
                    // liefer sie zurück
                    // entscheidung für übergebene Parameter
                    if (leftAABB.testAABB(rightAABBf)) {
                        i.collider.onCollide(i.getZAxis(), j)
                    }
                }
            }
        }
    }

    fun initialize() {
        lighting.initializeLights()

        for (i in transformables) {
            i.collider.initializeForm()
        }
    }

}