package ru.sipaha.engine.utils;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Iterator;

public class Shaders {

    public static final String DEFAULT_SHADER_NAME = "default_shader";
    private static ObjectMap<String, ShaderProgram> shadersByName = new ObjectMap<>();

    static {
        String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "uniform mat4 u_projTrans;\n" //
                + "varying vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   v_color.a = v_color.a * (256.0/255.0);\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n";
        String fragmentShader = "#ifdef GL_ES\n" //
                + "#define LOWP lowp\n" //
                + "precision mediump float;\n" //
                + "#else\n" //
                + "#define LOWP \n" //
                + "#endif\n" //
                + "varying LOWP vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "uniform sampler2D u_texture;\n" //
                + "void main()\n"//
                + "{\n" //
                + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
                + "}";

        ShaderProgram defaultShader = new ShaderProgram(vertexShader, fragmentShader);
        if (!defaultShader.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + defaultShader.getLog());
        }
        shadersByName.put(DEFAULT_SHADER_NAME, defaultShader);
    }

    public static ShaderProgram get(String name) {
        return shadersByName.get(name);
    }

    public static void add(String name, ShaderProgram shaderProgram) {
        shadersByName.put(name, shaderProgram);
    }

    public static Iterable<String> getNamesOfShaders() {
        return shadersByName.keys();
    }
}
