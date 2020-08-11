package cga.exercise.game

import cga.exercise.components.camera.Camera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.Lighting
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
import org.w3c.dom.Text
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    // TODO: Set static Variable for LightsourceCount or BoneCount
    private val shader: ShaderProgram = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
    private val anim_shader: ShaderProgram = ShaderProgram("assets/shaders/anim_vert.glsl", "assets/shaders/tron_frag.glsl")

    // Models
    var rend_ground: Renderable
    var troncam: Camera
    var human: AnimRenderable
    var monument: Renderable
    var example: Mesh? = null

    // Lights
    var pL_1: PointLight
    var sL_1: SpotLight

    // vars
    var xpos_old: Double
    var ypos_old: Double

    var lighting: Lighting

    //scene setup
    init {

        xpos_old = 0.0
        ypos_old = 0.0

        lighting = Lighting()

        val sphere = OBJLoader.loadOBJ("C:/Users/Julien/dev/cga/CGAFramework/assets/models/sphere.obj")
        val ground = OBJLoader.loadOBJ("C:/Users/Julien/dev/cga/CGAFramework/assets/models/ground.obj")
        monument = ModelLoader.loadModel("C:/Users/Julien/dev/cga/CGAFramework/assets/models/Monument.obj", 0f, 0f, 0f)!!

        //playermodel.scaleLocal(Vector3f(0.8f, 0.8f, 0.8f))

        // Model muss selbes Rig benutzen wie das loadModel Object
        human = ModelLoader.loadDAEModel("C:/Users/Julien/dev/cga/CGAFramework/assets/models/human.dae")
        human.scaleLocal(Vector3f(0.2f, 0.2f, 0.2f))
        human.shader = anim_shader
        human.animator.animations = ModelLoader.loadAnimations("C:/Users/Julien/dev/cga/CGAFramework/assets/models/Animations/walking.dae")
        human.rotateLocal(0f, Math.toRadians(180.0).toFloat(), 0f)

        // Material

        val diff = Texture2D("assets/textures/ground_diff.png", true)
        val spec = Texture2D("assets/textures/ground_spec.png", true)
        val emit = Texture2D("assets/textures/ground_emit.png", true)
        val tcMul = Vector2f(64f, 64f)
        diff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR, GL_LINEAR)
        spec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        emit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR, GL_LINEAR)
        val ground_material = Material(diff, emit, spec, 60.0f, tcMul)

        val attributes = arrayOf(
                VertexAttribute(3, GL_FLOAT, 32, 0),
                VertexAttribute(2, GL_FLOAT, 32, 3 * 4),
                VertexAttribute(3, GL_FLOAT, 32, 5 * 4)
        )

        val ground_m = Mesh(ground.objects[0].meshes[0].vertexData, ground.objects[0].meshes[0].indexData, attributes, ground_material)

        rend_ground = Renderable(arrayListOf(ground_m), Matrix4f(), null)

        // Lighting

        pL_1 = PointLight(Vector3f(0f, 0.5f, 0f), Vector3f(1f, 1f, 1f), human)
        sL_1 = SpotLight(Vector3f(0f, 0.2f, 0f), Vector3f(1f, 0f, 1f), human, 20f, 30f)

        lighting.add(sL_1)
        lighting.add(pL_1)
        lighting.initializeLights()

        // Camera

        troncam = Camera(human, (16f / 9f), Math.toRadians(90f.toDouble()).toFloat(), 0.1f, 100f)
        troncam.translateLocal(Vector3f(0f, 20f, 10f))
        troncam.rotateLocal(Math.toRadians(-20.0).toFloat(), 0f, 0f)
        troncam.rotateAroundPoint(0f, Math.toRadians(180.0).toFloat(), 0f, Vector3f(0f, 0f, 0f))

        // Debug Meshes
        human.collider!!.initializeForm(0)
        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLError.checkThrow()
        glDisable(GL_CULL_FACE); GLError.checkThrow()
        glFrontFace(GL_CCW); GLError.checkThrow()
        glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()
    }

    // TODO:Automatisierung der Rendercalls von Objekten (Liste?)
    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        shader.use()
        lighting.bind(shader)
        //shader.setUniform("emitcolor", Vector3f(0f, 0.75f + sin(t * 2.0).toFloat() / 6, 0.75f + cos(t * 2.0).toFloat() / 6))
        rend_ground.render(shader)
        monument.render(shader)
        troncam.bind(shader)

        example = human.collider!!.toMesh()
        human.collider!!.update(human.matrix)
        //print(human.getWorldModelMatrix())
        example!!.render()

        anim_shader.use()
        //anim_shader.setUniform("emitcolor", Vector3f(0f, 0.75f + sin(t * 2.0).toFloat() / 6, 0.75f + cos(t * 2.0).toFloat() / 6))
        lighting.bind(anim_shader)
        troncam.bind(anim_shader)
        human.render(anim_shader)
    }

    fun update(dt: Float, t: Float) {
        human.update(window, dt)
        // nächste Aufgabe: Kollisionsabfrage => Konzepte => Kollisionsbereich aus Vertices ableiten?

        if (window.getKeyState(GLFW.GLFW_KEY_W)) {
            human.translateLocal(Vector3f(0f, 0f, 0.1f))
        }

        if (window.getKeyState(GLFW.GLFW_KEY_A)) {
            human.rotateLocal(0f, Math.toRadians(90.0).toFloat() * dt, 0f)
        }

        if (window.getKeyState(GLFW.GLFW_KEY_D)) {
            human.rotateLocal(0f, Math.toRadians(-90.0).toFloat() * dt, 0f)
        }

        if (window.getKeyState(GLFW.GLFW_KEY_S)) {
            human.translateLocal(Vector3f(0f, 0f, -0.1f))
        }
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        val diff_x = xpos_old - xpos
        val diff_y = ypos_old - ypos

        //troncam.rotateAroundPoint(0f, diff_x.toFloat() * 0.002f, 0f, human.getYAxis())
        //troncam.rotateAroundPoint(diff_y.toFloat() * 0.002f,0f,0f, human.getXAxis())
        human.rotateLocal(0f, Math.toRadians(diff_x * 0.2).toFloat(), 0f)

        xpos_old = xpos
        ypos_old = ypos
    }

    fun cleanup() {
        shader.cleanup()
        anim_shader.cleanup()
    }
}
