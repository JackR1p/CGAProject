#version 330 core
#define LIGHTS_NUM 4
#define BONE_NUM 22
#pragma optionNV unroll all

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 tc;
layout(location = 2) in vec3 normal;
layout(location = 3) in ivec4 bone_index;
layout(location = 4) in vec4 weight;

//uniforms

// Animation
uniform mat4 Bones[BONE_NUM];

uniform mat4 model_matrix;
uniform mat4 view;
uniform mat4 proj;

uniform vec2 tcMultiplier;

out struct VertexData
{
    vec3 position;
    vec2 tc;
    vec3 normal;
} vertexData;

out vec3 Light_Camera_Direction;

out struct Light {
    vec3 position;
    vec3 color;
    vec3 lc_dir;// Light Camera Direction
    float intensity;
    float inner;
    float outer;
    vec3 spot_dir;
};

uniform Light Lights[LIGHTS_NUM];

out Light lights[LIGHTS_NUM];

out mat4 mmatrix;
out mat4 view_matrix;

void main(){
    lights = Lights;

    mmatrix = model_matrix;
    view_matrix = view;

    // Animation
    mat4 boneTransform = mat4(0);
    boneTransform[0][0] = 1;
    boneTransform[1][1] = 1;
    boneTransform[2][2] = 1;
    boneTransform[3][3] = 1;

    if (weight.x != 0){
        boneTransform = Bones[bone_index.x] * weight.x;
        boneTransform += Bones[bone_index.y] * weight.y;
        boneTransform += Bones[bone_index.z] * weight.z;
        boneTransform += Bones[bone_index.w] * weight.w;
    }

    // Texture Coordinates
    vertexData.tc = tc * tcMultiplier;

    // Transformations
    vec4 pos = model_matrix * boneTransform * vec4(position, 1.0f);

    // model view
    mat4 mv_mat = view * model_matrix;

    // Normal in Camera Perspective Transformation
    vertexData.normal = (inverse(transpose(mv_mat)) * boneTransform * vec4(normal, 1.0f)).xyz;

    vertexData.position = pos.xyz;

    // Camera View and Perspective Transformation
    gl_Position = proj * view * pos;

    // -- -- Light -- --
    vec4 lightsource = vec4(0, 0, 0, 0);

    vec4 P = view * pos;

    vec3 spot_dir = vec3(0, 0, 0);

    for (int i = 0; i < LIGHTS_NUM; i++){
        lightsource = view * vec4(lights[i].position, 1);
        lights[i].lc_dir = (lightsource - P).xyz;
        spot_dir = (inverse(transpose(view)) * vec4(lights[i].spot_dir, 1)).xyz;
        lights[i].spot_dir = spot_dir;
    }

    Light_Camera_Direction = -P.xyz;
}
