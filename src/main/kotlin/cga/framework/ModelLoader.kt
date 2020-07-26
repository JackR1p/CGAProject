package cga.framework

import cga.exercise.components.geometry.*
import cga.exercise.components.texture.Texture2D
import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.PointerBuffer
import org.lwjgl.assimp.*
import org.lwjgl.assimp.Assimp.*
import org.lwjgl.opengl.GL11
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import java.util.*
import kotlin.collections.ArrayList

object ModelLoader {
    private fun load(objPath: String): RawModel? {
        val rm = RawModel()
        try {
            val aiScene = Assimp.aiImportFile(objPath, Assimp.aiProcess_Triangulate or Assimp.aiProcess_GenNormals)
                    ?: return null
            // read materials
            for (m in 0 until aiScene.mNumMaterials()) {
                val rmat = RawMaterial()
                val tpath = AIString.calloc()
                val sceneMat = aiScene.mMaterials() ?: return null
                val mat = AIMaterial.create(sceneMat[m])
                Assimp.aiGetMaterialTexture(mat, Assimp.aiTextureType_DIFFUSE, 0, tpath, null as IntBuffer?, null, null, null, null, null)
                // diffuse texture
                var tpathj = tpath.dataString()
                if (rm.textures.contains(tpathj)) rmat.diffTexIndex = rm.textures.indexOf(tpathj) else {
                    rm.textures.add(tpathj)
                    rmat.diffTexIndex = rm.textures.size - 1
                }
                // specular texture
                Assimp.aiGetMaterialTexture(mat, Assimp.aiTextureType_SPECULAR, 0, tpath, null as IntBuffer?, null, null, null, null, null)
                tpathj = tpath.dataString()
                if (rm.textures.contains(tpathj)) rmat.specTexIndex = rm.textures.indexOf(tpathj) else {
                    rm.textures.add(tpathj)
                    rmat.specTexIndex = rm.textures.size - 1
                }
                // emissive texture
                Assimp.aiGetMaterialTexture(mat, Assimp.aiTextureType_EMISSIVE, 0, tpath, null as IntBuffer?, null, null, null, null, null)
                tpathj = tpath.dataString()
                if (rm.textures.contains(tpathj)) rmat.emitTexIndex = rm.textures.indexOf(tpathj) else {
                    rm.textures.add(tpathj)
                    rmat.emitTexIndex = rm.textures.size - 1
                }
                // shininess
                val sptr = PointerBuffer.allocateDirect(1)
                Assimp.aiGetMaterialProperty(mat, Assimp.AI_MATKEY_SHININESS, sptr)
                val sprop = AIMaterialProperty.create(sptr[0])
                rmat.shininess = sprop.mData().getFloat(0)
                rm.materials.add(rmat)
            }
            // read meshes
            val meshes = ArrayList<RawMesh>()
            for (m in 0 until aiScene.mNumMeshes()) {
                val sceneMeshes = aiScene.mMeshes() ?: return null
                val aiMesh = AIMesh.create(sceneMeshes[m])
                val mesh = RawMesh()
                // read vertices
                for (v in 0 until aiMesh.mNumVertices()) {
                    val aiVert = aiMesh.mVertices()[v]
                    val sceneNormals = aiMesh.mNormals() ?: return null
                    val aiNormal = sceneNormals[v]
                    val sceneTextureCoords = aiMesh.mTextureCoords(0) ?: return null
                    val aiTexCoord = if (aiMesh.mNumUVComponents(0) > 0) sceneTextureCoords[v] else null
                    val vert = Vertex(
                            Vector3f(aiVert.x(), aiVert.y(), aiVert.z()),
                            if (aiTexCoord != null) Vector2f(aiTexCoord.x(), aiTexCoord.y()) else Vector2f(0.0f, 0.0f),
                            Vector3f(aiNormal.x(), aiNormal.y(), aiNormal.z())
                    )
                    mesh.vertices.add(vert)
                }
                // read indices
                for (f in 0 until aiMesh.mNumFaces()) {
                    val face = aiMesh.mFaces()[f]
                    for (i in 0 until face.mNumIndices()) {
                        mesh.indices.add(face.mIndices()[i])
                    }
                }
                // material index
                mesh.materialIndex = aiMesh.mMaterialIndex()
                meshes.add(mesh)
            }
            // traverse assimp scene graph
            val nodeQueue: Queue<AINode> = LinkedList()
            nodeQueue.offer(aiScene.mRootNode())
            while (!nodeQueue.isEmpty()) {
                val node = nodeQueue.poll()
                for (m in 0 until node.mNumMeshes()) {
                    var sceneMeshes = node.mMeshes() ?: return null
                    rm.meshes.add(meshes[sceneMeshes[m]])
                }
                for (c in 0 until node.mNumChildren()) {
                    val sceneChildren = node.mChildren() ?: return null
                    val cnode = AINode.create(sceneChildren[c])
                    nodeQueue.offer(cnode)
                }
            }
        } catch (ex: Exception) {
            throw Exception("Something went terribly wrong. Thanks java.\n" + ex.message)
        }
        return rm
    }

    private fun flattenVertexData(vertices: List<Vertex>, rot: Matrix3f): FloatArray {
        val data = FloatArray(8 * vertices.size)
        var di = 0
        for ((position, texCoord, normal) in vertices) {
            position.mul(rot)
            normal.mul(Matrix3f(rot).transpose().invert())
            data[di++] = position.x
            data[di++] = position.y
            data[di++] = position.z
            data[di++] = texCoord.x
            data[di++] = texCoord.y
            data[di++] = normal.x
            data[di++] = normal.y
            data[di++] = normal.z
        }
        return data
    }

    private fun flattenAnimationVertexData(vertices: List<AnimationVertex>, rot: Matrix3f): ByteBuffer {
        val data = ByteBuffer.allocateDirect((64 * vertices.size)).order(ByteOrder.nativeOrder())
        for ((position, texCoord, normal, index, weight) in vertices) {
            position.mul(rot)
            normal.mul(Matrix3f(rot).transpose().invert())
            data.putFloat(position.x)
            data.putFloat(position.y)
            data.putFloat(position.z)
            data.putFloat(texCoord.x)
            data.putFloat(texCoord.y)
            data.putFloat(normal.x)
            data.putFloat(normal.y)
            data.putFloat(normal.z)
            data.putInt(index.x)
            data.putInt(index.y)
            data.putInt(index.z)
            data.putInt(index.w)
            data.putFloat(weight.x)
            data.putFloat(weight.y)
            data.putFloat(weight.z)
            data.putFloat(weight.w)
        }
        return data
    }

    private fun flattenIndexData(indices: List<Int>): IntArray {
        val data = IntArray(indices.size)
        var di = 0
        for (i in indices) {
            data[di++] = i
        }
        return data
    }

    fun loadModel(objpath: String, pitch: Float, yaw: Float, roll: Float): Renderable? {
        val model = load(objpath) ?: return null
        val textures = ArrayList<Texture2D>()
        val materials = ArrayList<Material>()
        val meshes = ArrayList<Mesh>()
        val stride = 8 * 4
        val atr1 = VertexAttribute(3, GL11.GL_FLOAT, stride, 0)
        val atr2 = VertexAttribute(2, GL11.GL_FLOAT, stride, 3 * 4)
        val atr3 = VertexAttribute(3, GL11.GL_FLOAT, stride, 5 * 4)
        val vertexAttributes = arrayOf(atr1, atr2, atr3)
        // preprocessing rotation
        val rot = Matrix3f().rotateZ(roll).rotateY(yaw).rotateX(pitch)
        // create textures
//default textures
        val ddata = BufferUtils.createByteBuffer(4)
        ddata.put(0.toByte()).put(0.toByte()).put(0.toByte()).put(0.toByte())
        ddata.flip()
        for (i in model.textures.indices) {
            if (model.textures[i].isEmpty()) {
                textures.add(Texture2D(ddata, 1, 1, true))
            } else {
                textures.add(Texture2D(objpath.substring(0, objpath.lastIndexOf('/') + 1) + model.textures[i], true))
            }
        }
        // materials
        for (i in model.materials.indices) {
            materials.add(Material(textures[model.materials[i].diffTexIndex],
                    textures[model.materials[i].emitTexIndex],
                    textures[model.materials[i].specTexIndex],
                    model.materials[i].shininess,
                    Vector2f(1.0f, 1.0f)))
        }
        // meshes
        for (i in model.meshes.indices) {
            meshes.add(Mesh(flattenVertexData(model.meshes[i].vertices, rot),
                    flattenIndexData(model.meshes[i].indices),
                    vertexAttributes,
                    materials[model.meshes[i].materialIndex]))
        }
        // assemble the renderable
        return Renderable(meshes)
    }

    fun loadDAEModel(objPath: String, pitch: Float, yaw: Float, roll: Float): AnimRenderable {
        val model = loadDAE(objPath)!!
        val textures = ArrayList<Texture2D>()
        val materials = ArrayList<Material>()
        val meshes = ArrayList<AnimationMesh>()
        val stride = 16 * 4
        val atr1 = VertexAttribute(3, GL11.GL_FLOAT, stride, 0)
        val atr2 = VertexAttribute(2, GL11.GL_FLOAT, stride, 3 * 4)
        val atr3 = VertexAttribute(3, GL11.GL_FLOAT, stride, 5 * 4)
        val atr4 = VertexAttribute(4, GL11.GL_UNSIGNED_INT, stride, 8 * 4)
        val atr5 = VertexAttribute(4, GL11.GL_FLOAT, stride, 12 * 4)
        val vertexAttributes = arrayOf(atr1, atr2, atr3, atr4, atr5)

        val rot = Matrix3f().rotateZ(roll).rotateY(yaw).rotateX(pitch)

        val ddata = BufferUtils.createByteBuffer(4)
        ddata.put(0.toByte()).put(0.toByte()).put(0.toByte()).put(0.toByte())
        ddata.flip()
        for (i in model.textures.indices) {
            if (model.textures[i].isEmpty()) {
                textures.add(Texture2D(ddata, 1, 1, true))
            } else {
                textures.add(Texture2D(objPath.substring(0, objPath.lastIndexOf('/') + 1) + model.textures[i], true))
            }
        }
        // materials
        for (i in model.materials.indices) {
            materials.add(Material(textures[model.materials[i].diffTexIndex],
                    textures[model.materials[i].emitTexIndex],
                    textures[model.materials[i].specTexIndex],
                    model.materials[i].shininess,
                    Vector2f(1.0f, 1.0f)))
        }
        // meshes
        for (i in model.meshes.indices) {
            meshes.add(AnimationMesh(flattenAnimationVertexData(model.meshes[i].vertices, rot),
                    flattenIndexData(model.meshes[i].indices),
                    vertexAttributes,
                    materials[model.meshes[i].materialIndex],
                    model.meshes[i].rootBone))
        }

        // assemble the renderable
        return AnimRenderable(meshes)
    }

    fun loadDAE(objPath: String): RawAnimationModel? {
        val rm = RawAnimationModel()
        try {
            val aiScene = aiImportFile(objPath, aiProcess_Triangulate)
                    ?: return null
            val meshes = ArrayList<RawAnimationMesh>()

            // read Material
            for (m in 0 until aiScene.mNumMaterials()) {
                val rmat = RawMaterial()
                val tpath = AIString.calloc()
                val sceneMat = aiScene.mMaterials() ?: return null
                val mat = AIMaterial.create(sceneMat[m])
                Assimp.aiGetMaterialTexture(mat, Assimp.aiTextureType_DIFFUSE, 0, tpath, null as IntBuffer?, null, null, null, null, null)
                // diffuse texture
                var tpathj = tpath.dataString()
                if (rm.textures.contains(tpathj)) rmat.diffTexIndex = rm.textures.indexOf(tpathj) else {
                    rm.textures.add(tpathj)
                    rmat.diffTexIndex = rm.textures.size - 1
                }
                // specular texture
                Assimp.aiGetMaterialTexture(mat, Assimp.aiTextureType_SPECULAR, 0, tpath, null as IntBuffer?, null, null, null, null, null)
                tpathj = tpath.dataString()
                if (rm.textures.contains(tpathj)) rmat.specTexIndex = rm.textures.indexOf(tpathj) else {
                    rm.textures.add(tpathj)
                    rmat.specTexIndex = rm.textures.size - 1
                }
                // emissive texture
                Assimp.aiGetMaterialTexture(mat, Assimp.aiTextureType_EMISSIVE, 0, tpath, null as IntBuffer?, null, null, null, null, null)
                tpathj = tpath.dataString()
                if (rm.textures.contains(tpathj)) rmat.emitTexIndex = rm.textures.indexOf(tpathj) else {
                    rm.textures.add(tpathj)
                    rmat.emitTexIndex = rm.textures.size - 1
                }
                // shininess
                val sptr = PointerBuffer.allocateDirect(1)
                Assimp.aiGetMaterialProperty(mat, Assimp.AI_MATKEY_SHININESS, sptr)
                val sprop = AIMaterialProperty.create(sptr[0])
                rmat.shininess = sprop.mData().getFloat(0)
                rm.materials.add(rmat)
            }

            // read meshes
            for (m in 0 until aiScene.mNumMeshes()) {
                val aiSceneMeshes = aiScene.mMeshes()!!
                val cur_mesh = AIMesh.create(aiSceneMeshes[m])

                val mesh = RawAnimationMesh()

                // read Bones from mesh
                val format_vwi = formatBoneData(cur_mesh)
                val bones = mutableListOf<Bone>()
                for (o in 0 until cur_mesh.mNumBones()) {
                    val aibone = AIBone.create(cur_mesh.mBones()!![o])
                    bones.add(Bone(o, aibone.mName().dataString(), mutableListOf(), Convert.AiToJOML(aibone.mOffsetMatrix())))
                }

                // read Bone Hierarchy and Matrices from AIScene
                val rNode = AINode.create(aiScene.mRootNode()!!.mChildren()!![3])
                val rootBone = traverseBoneTree(rNode, bones)
                mesh.rootBone = rootBone

                //koutBoneTree(rootBone)

                val bone_index = mutableListOf<Vector4i>()
                val weights = mutableListOf<Vector4f>()
                for (e in 0 until format_vwi.size) {
                    if (((e + 1) % 4) == 0) {
                        bone_index.add(Vector4i(format_vwi[e - 3].y.toInt(), format_vwi[e - 2].y.toInt(), format_vwi[e - 1].y.toInt(), format_vwi[e].y.toInt()))
                        weights.add(Vector4f(format_vwi[e - 3].z, format_vwi[e - 2].z, format_vwi[e - 1].z, format_vwi[e].z))
                    }
                }
                // ------
                for (v in 0 until cur_mesh.mNumVertices()) {
                    val aiVert = cur_mesh.mVertices()[v]
                    val aiNorm = cur_mesh.mNormals()!![v]
                    val sceneTextureCoords = cur_mesh.mTextureCoords(0)!!
                    val aiTexCoord = if (cur_mesh.mNumUVComponents(0) > 0) sceneTextureCoords[v] else null

                    val vert = AnimationVertex()

                    vert.position = Vector3f(aiVert.x(), aiVert.y(), aiVert.z())
                    vert.normal = Vector3f(aiNorm.x(), aiNorm.y(), aiNorm.z())
                    vert.texCoord = if (aiTexCoord != null) Vector2f(aiTexCoord.x(), aiTexCoord.y()) else Vector2f()
                    vert.index = bone_index[v]
                    vert.weight = weights[v]
                    mesh.vertices.add(vert)
                }

                for (f in 0 until cur_mesh.mNumFaces()) {
                    val face = cur_mesh.mFaces()[f]
                    for (i in 0 until face.mNumIndices()) {
                        mesh.indices.add(face.mIndices()[i])
                    }
                }

                // material index
                mesh.materialIndex = cur_mesh.mMaterialIndex()
                meshes.add(mesh)
            }

            // traverse assimp scene graph
            val nodeQueue: Queue<AINode> = LinkedList()
            nodeQueue.offer(aiScene.mRootNode())
            while (!nodeQueue.isEmpty()) {
                val node = nodeQueue.poll()
                for (m in 0 until node.mNumMeshes()) {
                    val sceneMeshes = node.mMeshes() ?: return null
                    rm.meshes.add(meshes[sceneMeshes[m]])
                }
                for (c in 0 until node.mNumChildren()) {
                    val sceneChildren = node.mChildren() ?: return null
                    val cnode = AINode.create(sceneChildren[c])
                    nodeQueue.offer(cnode)
                }
            }

        } catch (e: java.lang.Exception) {
            print("Failed to load Animation Mesh: $e")
        }
        return rm
    }

    fun formatBoneData(cur_mesh: AIMesh): MutableList<Vector3f> {
        val vwi: MutableList<Vector3f> = mutableListOf()
        for (o in 0 until cur_mesh.mNumBones()) {
            val aibone = AIBone.create(cur_mesh.mBones()!![o])
            val weights = aibone.mWeights()
            // Formating Weights/Indexes
            for (e in 0 until aibone.mNumWeights()) {
                vwi.add(Vector3f(weights[e].mVertexId().toFloat(), o.toFloat(), weights[e].mWeight()))
            }
        }
        vwi.sortByDescending { x -> x.z }
        vwi.sortBy { x -> x.x }
        // Format Bones and Weights
        val format_vwi: MutableList<Vector3f> = mutableListOf()
        var tmp_id: Int = 0
        var vnum: Int = 0
        // Formatiert die VertexID-Weight-boneIndex (vwi) Liste => einer VertexID werden jeweils 4 Einträge von BoneIndex & Weight zugeordnet
        // sodass die Größe von format_vwi gleich der Größe von cur_mesh.mNumVertices ist. Diese kann dann in den VertexShader geladen werden
        // TODO: Fehler fixen: Letzte Einträge werden wegen if statement nicht mitgenommen
        for (e in 0 until vwi.size) {
            val cur_weight = vwi[e]
            if (vwi[e].x != tmp_id.toFloat()) {
                if (vnum < 4) {
                    for (k in 0 until 4 - vnum) {
                        format_vwi.add(Vector3f(tmp_id.toFloat(), 0f, 0f))
                    }
                }
                tmp_id = cur_weight.x.toInt()
                vnum = 0
            }
            if (vnum < 4) {
                format_vwi.add(cur_weight)
            }
            vnum++
        }
        return format_vwi
    }

    fun traverseBoneTree(node: AINode, complete_bonelist: MutableList<Bone>): Bone {
        // node sollte Root Node sein
        var cur_bone = Bone()
        for (x in 0 until complete_bonelist.size) {
            if (node.mName().dataString() == complete_bonelist[x].name) {
                cur_bone = Bone(complete_bonelist[x].id, name = node.mName().dataString(), offset = complete_bonelist[x].offset,
                        transform = Convert.AiToJOML(node.mTransformation()))
                break
            } else {
                cur_bone = Bone(name = node.mName().dataString(), transform = Convert.AiToJOML(node.mTransformation()))
            }
        }

        for (x in 0 until node.mNumChildren()) {
            val cur_child = AINode.create(node.mChildren()!![x])
            cur_bone.children.add(traverseBoneTree(cur_child, complete_bonelist))
        }
        return cur_bone
    }

    fun koutBoneTree(node: Bone) {
        print(" " + node.name + " NUMC:" + node.children.size + " Offset" + node.offset)

        for (x in 0 until node.children.size) {
            koutBoneTree(node.children[x])
        }

    }

    fun loadAnimations(objPath: String): Array<Animation> {
        val animationList = mutableListOf<Animation>()
        try {
            val aiScene = aiImportFile(objPath, aiProcess_Triangulate)!!

            var curAnimation: AIAnimation
            var curChannel: AINodeAnim
            for (x in 0 until aiScene.mNumAnimations()) {
                curAnimation = AIAnimation.create(aiScene.mAnimations()!![x])
                val bone_transforms = mutableListOf<JointTransform>()
                val animation = Animation()
                if (curAnimation.mName().dataString() != "") {
                    animation.name = curAnimation.mName().dataString()
                } else {
                    animation.name = "Animation$x"
                }
                animation.durotation = curAnimation.mDuration()
                animation.ticksPerSecond = curAnimation.mTicksPerSecond()

                for (y in 0 until curAnimation.mNumChannels()) {
                    curChannel = AINodeAnim.create(curAnimation.mChannels()!![y])
                    val curBone = curChannel.mNodeName().dataString()
                    for (z in 0 until curChannel.mNumPositionKeys()) {
                        val curPos = curChannel.mPositionKeys()!![z]
                        val curRot = curChannel.mRotationKeys()!![z]

                        bone_transforms.add(JointTransform(curPos.mTime(), curBone, Convert.AIToJOML(curPos.mValue()), Convert.AIToJOML(curRot.mValue())))
                    }
                }

                bone_transforms.sortBy { it.timestamp } // size 640 jeweils 20 Einträge für einen Timestamp

                val keyframes = mutableListOf<Keyframe>()
                var jtransforms = mutableMapOf<String, JointTransform>()
                var timestamp: Double = 0.0
                for (y in 0 until bone_transforms.size / 20) {
                    bone_transforms.slice((y * 20) until (y + 1) * 20).forEach {
                        jtransforms[it.node] = it
                        timestamp = it.timestamp
                    }
                    keyframes.add(Keyframe(timestamp, jtransforms))
                    jtransforms = mutableMapOf()
                }
                animation.keyframes = keyframes
                //animation.keyframes.forEach { it.transformations.forEach { x -> print(" " + x.key + " " + x.value.timestamp)} }
                animationList.add(animation)
            }

        } catch (e: java.lang.Exception) {
            print("Error Loading Animation" + e.message)
        }
        return animationList.toTypedArray()
    }
}