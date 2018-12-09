package proj.idfk.shader;

import proj.idfk.util.Disposable;

import java.io.InputStream;
import java.util.Scanner;

import static org.lwjgl.opengl.GL45.*;

public class ShaderProgram implements Disposable {
    private final int programID;

    public ShaderProgram(String vertex, String fragment) {
        this.programID = glCreateProgram();

        int vertexID = loadShaderProgram(vertex, GL_VERTEX_SHADER);
        int fragmentID = loadShaderProgram(fragment, GL_FRAGMENT_SHADER);

        glAttachShader(programID, vertexID);
        glAttachShader(programID, fragmentID);

        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            System.out.println(vertex + " " + fragment + ": linking failed");
            System.out.println(glGetProgramInfoLog(programID));
            return;
        }

        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
    }

    public void getAllUniformLocations(Uniform... uniforms) {
        for (Uniform uniform : uniforms) {
            uniform.getUniformLocation(this.programID);
        }
    }

    public void bind() {
        glUseProgram(programID);
    }

    private int loadShaderProgram(String fileName, int type) {
        try (final InputStream in = ShaderProgram.class.getResourceAsStream("/shaders/" + fileName)) {
            Scanner scn = new Scanner(in);
            StringBuilder sb = new StringBuilder();

            while (scn.hasNext()) {
                sb.append(scn.nextLine()).append("\n");
            }

            scn.close();

            int shader = glCreateShader(type);
            glShaderSource(shader, sb.toString());
            glCompileShader(shader);

            if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
                System.out.println("Shader compilation failed");
                System.out.println(glGetShaderInfoLog(shader));
                return -1;
            } else {
                return shader;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void dispose() {
        glUseProgram(0);
        glDeleteProgram(programID);
    }
}
