package proj.idfk.player;

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;
import proj.idfk.Config;
import proj.idfk.Window;
import proj.idfk.entity.Entity;
import proj.idfk.render.MasterRenderer;
import proj.idfk.world.BlockID;
import proj.idfk.world.World;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {
    private static final float speed = 0.5f;
    private Vector3f acceleration;
    private final Config config;
    private final World world;
    private boolean isOnGround = false;


    public Player(Config config, Vector3f position, World world) {
        super(position, new Vector3f(-30, 0, 0), new Vector3f(0.3f, 0.1f, 0.3f));
        this.config = config;
        this.world = world;
        this.acceleration = new Vector3f();
    }

    public void update(float delta) {
        velocity.add(acceleration);
        acceleration.zero();

        if (!isOnGround) {
            velocity.y -= 40 * delta;
        }
        isOnGround = false;

        if (position.y < -100) {
            position.y = 300;
        }

        position.x += velocity.x * delta;
        collide(new Vector3f(velocity.x, 0, 0), delta);

        position.y += velocity.y * delta;
        collide(new Vector3f(0, velocity.y, 0), delta);

        position.z += velocity.z * delta;
        collide(new Vector3f(0, 0, velocity.z), delta);

        velocity.x *= 0.95f;
        velocity.z *= 0.95f;
    }

    private void collide(Vector3f vel, float dt)
    {
        for (int x = (int) (position.x - dimensions.x); x < position.x + dimensions.x; x++)
            for (int y = (int) (position.y - dimensions.y); y < position.y + 1.7f; y++)
                for (int z = (int) (position.z - dimensions.z); z < position.z + dimensions.z; z++)
                {
                    byte block = world.getBlock(x, y, z);

                    if (block != 0 && BlockID.getBlockData(block).isCollidable)
                    {
                        if (vel.y > 0)
                        {
                            position.y = y - dimensions.y;
                            velocity.y = 0;
                        }
                        else if (vel.y < 0)
                        {
                            isOnGround = true;
                            position.y = y + dimensions.y + 1;
                            velocity.y = 0;
                        }

                        if (vel.x > 0)
                        {
                            position.x = x - dimensions.x;
                        }
                        else if (vel.x < 0)
                        {
                            position.x = x + dimensions.x + 1;
                        }

                        if (vel.z > 0)
                        {
                            position.z = z - dimensions.z;
                        }
                        else if (vel.z < 0)
                        {
                            position.z = z + dimensions.z + 1;
                        }
                    }
                }
    }

    @SuppressWarnings("EmptyMethod")
    public void render(MasterRenderer renderer) {
        //TODO render player
    }

    public void scroll(float yoffset) {

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
        if (window.isKeyDown(GLFW_KEY_SPACE)) {
            jump();
        }
    }

    public void jump() {
        if (isOnGround) {
            isOnGround = false;
            acceleration.y += speed * 25f;
        }
    }

    private static Vector2f lastMousePosition = new Vector2f();
    private static final float BOUND = 89.9999f;


    @SuppressWarnings("SuspiciousNameCombination")
    private void mouseInput(Window window) {
        Vector2f change = new Vector2f(window.getCursorPosition()).sub(lastMousePosition).mul(config.sensitivity);

        rotation.y += change.x;
        rotation.x += change.y;

        if (rotation.x > BOUND) {
            rotation.x = BOUND;
        } else if(rotation.x < -BOUND) {
            rotation.x = -BOUND;
        }

        if (rotation.y > 360) rotation.y = rotation.y - 360f;
        else if (rotation.y < 0) rotation.y = rotation.y + 360f;

        lastMousePosition = new Vector2f(window.getCursorPosition());
    }
}
