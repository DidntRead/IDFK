package proj.idfk.shader;

import org.lwjgl.opengl.GL45;

public abstract class Uniform {
    protected int location;
    protected int programID;
    private final String name;

    protected Uniform(String name) {
        this.name = name;
    }

    protected void getUniformLocation(int programID) {
        this.location = GL45.glGetUniformLocation(programID, this.name);
        this.programID = programID;
        if (this.location == -1) {
            System.err.println("No uniform variable called " + this.name + " found!");
        }
    }
}
