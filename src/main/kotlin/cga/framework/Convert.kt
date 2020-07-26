package cga.framework

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
import org.lwjgl.assimp.AIMatrix4x4
import org.lwjgl.assimp.AIQuaternion
import org.lwjgl.assimp.AIVector3D

object Convert {

    fun AiToJOML(aimat: AIMatrix4x4): Matrix4f {
        val res = Matrix4f()
        res.m00(aimat.a1())
        res.m01(aimat.a2())
        res.m02(aimat.a3())
        res.m03(aimat.a4())
        res.m10(aimat.b1())
        res.m11(aimat.b2())
        res.m12(aimat.b3())
        res.m13(aimat.b4())
        res.m20(aimat.b1())
        res.m21(aimat.b2())
        res.m22(aimat.b3())
        res.m23(aimat.b4())
        res.m30(aimat.b1())
        res.m31(aimat.b2())
        res.m32(aimat.b3())
        res.m33(aimat.b4())
        return res
    }

    fun AIToJOML(aivec : AIVector3D) : Vector3f {
        val res = Vector3f()
        res.x = aivec.x()
        res.y = aivec.y()
        res.z = aivec.z()
        return res;
    }

    fun AIToJOML(aiquat : AIQuaternion): Quaternionf {
        val res = Quaternionf()
        res.x = aiquat.x()
        res.y = aiquat.y()
        res.z = aiquat.z()
        res.w = aiquat.w()
        return res
    }
}