#version 330 core
#define LIGHTS_NUM 2
#pragma optionNV unroll all

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 tc;
layout(location = 2) in vec3 normal;

//uniforms

// translation object to world
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
    float c_att;
    float l_att;
    float q_att;
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

    // Texture Coordinates
    vertexData.tc = tc * tcMultiplier;

    // Transformations
    vec4 pos = model_matrix * vec4(position, 1.0f);

    // model view
    mat4 mv_mat = view * model_matrix;

    // Normal in Camera Perspective Transformation
    vertexData.normal = (inverse(transpose(mv_mat)) * vec4(normal, 1.0f)).xyz;

    vertexData.position = pos.xyz;

    // Camera View and Perspective Transformation
    gl_Position = proj * view * pos;

    // -- -- Light -- --
    vec4 lightsource = vec4(0, 0, 0, 0);

    vec4 P = view * pos;

    vec3 spot_dir = vec3(0,0,0);
    mat4 invTransView = inverse(transpose(view));


    for (int i = 0; i < LIGHTS_NUM; i++){
        lightsource = view * vec4(lights[i].position, 1);
        lights[i].lc_dir = (lightsource - P).xyz;

        spot_dir = (invTransView * vec4(lights[i].spot_dir, 1)).xyz;
        lights[i].spot_dir = spot_dir;
    }

    Light_Camera_Direction = -P.xyz;
}
