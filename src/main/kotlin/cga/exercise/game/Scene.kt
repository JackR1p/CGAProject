package cga.exercise.game

import cga.exercise.components.SceneControl.SceneControl
import cga.exercise.components.camera.Camera
import cga.exercise.components.gObjects.Player
import cga.exercise.components.geometry.*
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.*
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.*

/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {

    val sceneCtrl = SceneControl()

    // TODO: Set static Variable for LightsourceCount or BoneCount
    private val shader: ShaderProgram = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
    private val anim_shader: ShaderProgram = ShaderProgram("assets/shaders/anim_vert.glsl", "assets/shaders/tron_frag.glsl")

    // Models
    var rend_ground: Renderable
    var human: AnimRenderable
    var player: Player
    var monument: Renderable

    val darkness_modifier = 0.1f

    // camera
    var troncam: Camera

    // Lights
    var pL_1: PointLight
    var sL_1: SpotLight

    // vars
    var xpos_old: Double
    var ypos_old: Double

    //scene setup
    init {

        xpos_old = 0.0
        ypos_old = 0.0

        monument = ModelLoader.loadModel("C:/Users/Julien/dev/cga/CGAFramework/assets/models/Monument.obj", 0f, 0f, 0f)!!


        //playermodel.scaleLocal(Vector3f(0.8f, 0.8f, 0.8f))

        // Model muss selbes Rig benutzen wie das loadModel Object (Animation)
        human = ModelLoader.loadDAEModel("C:/Users/Julien/dev/cga/CGAFramework/assets/models/human.dae")
        player = Player(human)
        player.shader = anim_shader
        player.animator.animations = ModelLoader.loadAnimations("C:/Users/Julien/dev/cga/CGAFramework/assets/models/Animations/walking.dae")

        rend_ground = ModelLoader.loadModel("C:/Users/Julien/dev/cga/CGAFramework/assets/models/dirt.obj", 0f, 0f, 0f)!!

        // Lighting

        pL_1 = PointLight(Vector3f(0f, 50f, 0f), Vector3f(1f, 1f, 1f), rend_ground)
        sL_1 = SpotLight(Vector3f(0f, 0.5f, 0f), Vector3f(1f, 1f, 1f), player, 20f, 30f)
        sL_1.intensity = 5f
        pL_1.intensity = 20f

        // Camera

        troncam = Camera(player, (16f / 9f), Math.toRadians(90f.toDouble()).toFloat(), 0.1f, 100f)
        troncam.translateLocal(Vector3f(0f, 20f, 10f))
        troncam.rotateLocal(Math.toRadians(-20.0).toFloat(), 0f, 0f)
        troncam.rotateAroundPoint(0f, Math.toRadians(180.0).toFloat(), 0f, Vector3f(0f, 0f, 0f))

        // Collision Forms
        player.collider.type = 1
        player.name = "player"
        monument.collider.type = 0
        monument.name = "monument"
        rend_ground.collider.type = 0
        rend_ground.name = "ground"

        player.scaleLocal(Vector3f(0.2f, 0.2f, 0.2f))

        // Initialize Lighting and CollisionForms
        sceneCtrl.lighting.add(sL_1)
        sceneCtrl.lighting.add(pL_1)
        sceneCtrl.transformables.add(player)
        sceneCtrl.transformables.add(monument)
        sceneCtrl.transformables.add(rend_ground)
        sceneCtrl.updatable.add(player)
        sceneCtrl.updatable.add(monument)
        sceneCtrl.updatable.add(rend_ground)
        sceneCtrl.initialize()

        player.translateGlobal(Vector3f(5f, 0.4f, 0f))

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLError.checkThrow()
        glDisable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()
    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        shader.use()
        shader.setUniform("darkness_modifier", darkness_modifier)
        sceneCtrl.lighting.bind(shader)
        //sceneCtrl.renderCollisionBoxes(shader)

        // Shader Objects
        rend_ground.render(shader)
        monument.render(shader)
        troncam.bind(shader)

        anim_shader.use()
        anim_shader.setUniform("darkness_modifier", darkness_modifier)
        sceneCtrl.lighting.bind(anim_shader)
        troncam.bind(anim_shader)

        // Animation Shader Objects
        player.render(anim_shader)
    }

    fun update(dt: Float, t: Float) {
        sceneCtrl.update(window, dt, t)
        sceneCtrl.collision()

        if (window.getKeyState(GLFW.GLFW_KEY_W)) {
            player.translateLocal(Vector3f(0f, 0f, 0.1f))
        }

        if (window.getKeyState(GLFW.GLFW_KEY_A)) {
            player.rotateLocal(0f, Math.toRadians(90.0).toFloat() * dt, 0f)
        }

        if (window.getKeyState(GLFW.GLFW_KEY_D)) {
            player.rotateLocal(0f, Math.toRadians(-90.0).toFloat() * dt, 0f)
        }

        if (window.getKeyState(GLFW.GLFW_KEY_S)) {
            player.translateLocal(Vector3f(0f, 0f, -0.1f))
        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        val diff_x = xpos_old - xpos
        val diff_y = ypos_old - ypos

        troncam.rotateAroundPoint(0f, diff_x.toFloat() * 0.002f, 0f, player.getYAxis())
        //troncam.rotateAroundPoint(diff_y.toFloat() * 0.002f,0f,0f, human.getXAxis())
        //human.rotateLocal(0f, Math.toRadians(diff_x * 0.2).toFloat(), 0f)

        xpos_old = xpos
        ypos_old = ypos
    }

    fun cleanup() {
        shader.cleanup()
        anim_shader.cleanup()
    }
}
