#version 330 core
#define LIGHTS_NUM 2
#pragma optionNV unroll all
//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec2 tc;
    vec3 normal;
} vertexData;

in vec3 Light_Camera_Direction;

struct Light {
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

in mat4 mmatrix;
in mat4 view_matrix;
in Light lights[LIGHTS_NUM];

uniform float shininess;

uniform sampler2D emit;
uniform sampler2D specular;
uniform sampler2D diffuse;

vec3 emit_col;
vec3 diff_col;
vec3 spec_col;

uniform float darkness_modifier;

//fragment shader output
out vec4 color;

// Methoden
vec3 calcPointLight(vec3 Light_Direction, vec3 Light_Color, vec3 Normal, vec3 View_Direction, float c_att, float l_att, float q_att, float intensity, float boolean);
vec3 calcSpotLight(vec3 Light_Direction, vec3 Spot_Direction, vec3 Light_Color, vec3 Normal, vec3 View_Direction, float c_att, float l_att, float q_att, float intensity, float outerAngle, float innerAngle);

void main(){

    vec3 N = normalize(vertexData.normal);
    vec3 V = normalize(Light_Camera_Direction);

    // Texturen
    emit_col = texture2D(emit, vertexData.tc).rgb;
    diff_col = texture2D(specular, vertexData.tc).rgb;
    spec_col = texture2D(diffuse, vertexData.tc).rgb;

    vec3 result = vec3(0, 0, 0);
    for (int i = 0; i < LIGHTS_NUM; i++){
        result += calcPointLight(lights[i].lc_dir, lights[i].color, N, V, lights[i].c_att, lights[i].l_att, lights[i].q_att, lights[i].intensity, lights[i].inner);
        result += calcSpotLight(lights[i].lc_dir, lights[i].spot_dir, lights[i].color, N, V, lights[i].c_att, lights[i].l_att, lights[i].q_att, lights[i].intensity, lights[i].outer, lights[i].inner);
    }

    result += emit_col * darkness_modifier;
    result += diff_col * 0.01f;
    color = vec4(result, 1.0);
}

vec3 calcPointLight(vec3 Light_Direction, vec3 Light_Color, vec3 Normal, vec3 View_Direction, float c_att, float l_att, float q_att, float intensity, float boolean){

    if (boolean != 0){
        return vec3(0, 0, 0);
    }

    float Light_Distance = length(Light_Direction);
    Light_Direction = normalize(Light_Direction);

    // Vektor des am Fragment reflektierten Licheinfallsvektors
    vec3 R = normalize(reflect(-Light_Direction, Normal));

    // Winkel zwischen Licht-Einfallsvektor und Normalen => alpha
    float alpha = max(0.0, dot(Light_Direction, Normal));

    // cos^k beta
    float beta = pow(max(0, dot(View_Direction, R)), shininess);

    // diffuser Term
    vec3 diff_term = diff_col * alpha * intensity;

    // spekularer Term
    vec3 specular_term = spec_col * beta * intensity;

    // Attenuation
    float attenuation = 1.0 / (c_att + l_att * Light_Distance + q_att * (Light_Distance * Light_Distance));

    // result
    vec3 result = diff_term;
    result += specular_term;
    result *= Light_Color * attenuation;
    return result;
}

vec3 calcSpotLight(vec3 Light_Direction, vec3 Spot_Direction, vec3 Light_Color, vec3 Normal, vec3 View_Direction, float c_att, float l_att, float q_att, float intensity, float outerAngle, float innerAngle){

    if (innerAngle == 0) {
        return vec3(0, 0, 0);
    }

    // Selber Code wie in Spotlight (Berechnung der Phong Komponenten + Attenuation)
    float Light_Distance = length(Light_Direction);
    Light_Direction = normalize(Light_Direction);
    vec3 R = normalize(reflect(-Light_Direction, Normal));
    float alpha = max(0.0, dot(Light_Direction, Normal));
    float beta = pow(max((dot(View_Direction, R)), 0.0), shininess);
    float attenuation = 1.0 / (c_att + l_att * Light_Distance + q_att * (Light_Distance * Light_Distance));

    // Winkel zwischen Spo
    float theta = dot(normalize(Spot_Direction), normalize(-Light_Direction));
    float SpotIntensity = 0;

    if (theta > outerAngle){
        SpotIntensity = clamp(((theta - outerAngle) / (innerAngle - outerAngle)), 0, 1);
    } else {
        SpotIntensity = 0;
    }

    vec3 diff_term = diff_col * alpha * intensity * SpotIntensity;
    vec3 spec_term = spec_col * beta * intensity * SpotIntensity;

    vec3 result = diff_term;
    result += spec_term;
    result *= Light_Color * attenuation;
    return result;
}