package proj.idfk.entity;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;
import proj.idfk.Config;
import proj.idfk.Window;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {
    private static final float speed = 1f;
    private Vector3f acceleration;
    private Config config;


    public Player(Config config) {
        super(new Vector3f(0, 6, 0), new Vector3f(0, 0, 0), new Vector3f(0.3f, 1, 0.3f));
        this.config = config;
        this.acceleration = new Vector3f();
    }

    public void update(float delta) {
        velocity.add(acceleration);
        acceleration.zero();

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
        position.z += velocity.z * delta;

        box.setMin(position);

        velocity.x *= 0.95f;
        velocity.z *= 0.95f;
    }

    @SuppressWarnings("EmptyMethod")
    public void render() {
        //TODO render player
    }

    public void handleInput(Window window) {
        mouseInput(window);
        keyboardInput(window);
    }

    private void keyboardInput(Window window) {
        if (window.isKeyDown(GLFW_KEY_W)) {
            float s = speed;
            if (window.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
                s /= 5;
            }
            acceleration.x += -Math.cos(Math.toRadians(rotation.y + 90)) * s;
            acceleration.z += -Math.sin(Math.toRadians(rotation.y + 90)) * s;
        }
        if (window.isKeyDown(GLFW_KEY_S)) {
            acceleration.x += Math.cos(Math.toRadians(rotation.y + 90)) * speed;
            acceleration.z += Math.sin(Math.toRadians(rotation.y + 90)) * speed;
        }
        if (window.isKeyDown(GLFW_KEY_A)) {
            acceleration.x += -Math.cos(Math.toRadians(rotation.y)) * speed;
            acceleration.z += -Math.sin(Math.toRadians(rotation.y)) * speed;
        }
        if (window.isKeyDown(GLFW_KEY_D)) {
            acceleration.x += Math.cos(Math.toRadians(rotation.y)) * speed;
            acceleration.z += Math.sin(Math.toRadians(rotation.y)) * speed;
        }

        if (window.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            position.y -= 0.02f;
        } else if (window.isKeyDown(GLFW_KEY_SPACE)) {
            position.y += 0.02f;
        }
    }

    private static Vector2f lastMousePosition = new Vector2f();
    private static final float BOUND = 89.9999f;

    private void mouseInput(Window window) {
        Vector2f change = new Vector2f(window.getCursorPosition()).sub(lastMousePosition);

        rotation.y += change.x * config.sensitivity;
        rotation.x += change.y * config.sensitivity;

        if (rotation.x > BOUND) rotation.x = BOUND;
        else if(rotation.x < -BOUND) rotation.x = -BOUND;

        if (rotation.y > 360) rotation.y = rotation.y - 360f;
        else if (rotation.y < 0) rotation.y = rotation.y + 360f;

        lastMousePosition = new Vector2f(window.getCursorPosition());
    }
}
