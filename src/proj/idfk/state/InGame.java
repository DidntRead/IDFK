package proj.idfk.state;

import org.joml.Vector3f;
import proj.idfk.Application;
import proj.idfk.Camera;
import proj.idfk.callback.KeyCallback;
import proj.idfk.callback.MouseButtonCallback;
import proj.idfk.callback.ScrollCallback;
import proj.idfk.render.MasterRenderer;
import proj.idfk.util.Ray;
import proj.idfk.util.Timer;
import proj.idfk.world.BlockID;
import proj.idfk.world.save.SaveManager;
import proj.idfk.world.World;

import static org.lwjgl.glfw.GLFW.*;

public class InGame implements GameState, KeyCallback, ScrollCallback {
    private final Application app;
    private final SaveManager saveManager;
    private final Timer breakTimer;
    private World world = null;

    public InGame(Application app, SaveManager saveManager) {
        this.app = app;
        this.breakTimer = new Timer();
        this.saveManager = saveManager;
    }

    @Override
    public void on_enter() {
        this.world = saveManager.getCurrentWorld();
        app.getWindow().disableNuklearInput();
        app.getCamera().hookEntity(world.getPlayer());
        app.getRenderer().setInGame(true);
    }

    @Override
    public void on_exit() {
        app.getRenderer().setInGame(false);
        app.getCamera().hookEntity(null);
        app.getWindow().enableNuklearInput();
    }

    @Override
    public void update(float delta) {
        world.getPlayer().handleInput(app.getWindow());
        world.getPlayer().update(delta);

        for (Ray ray = new Ray(app.getCamera().position, app.getCamera().rotation); ray.getLength() < 4; ray.step(0.05f)) {
            int x = Math.round(ray.getEnd().x);
            int y = Math.round(ray.getEnd().y);
            int z = Math.round(ray.getEnd().z);
            byte block = world.getBlock(x, y, z);

            if (block != BlockID.AIR) {
                if (breakTimer.elapsedWithoutReset() > 0.2f) {
                    if (app.getWindow().isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                        breakTimer.reset();
                        world.setBlock(x, y, z, (byte) 0);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void render(MasterRenderer renderer, Camera camera) {
        world.getPlayer().render(renderer);
        world.render(app, camera);
    }

    @Override
    public void keyCallback(int key, int action, int modifiers) {
        if (action == GLFW_PRESS) {
            if (key == GLFW_KEY_ESCAPE) {
                app.getStateManager().push(GameStateManager.GameState.PauseMenu);
            }
            if (key == GLFW_KEY_SPACE) {
                world.getPlayer().jump();
            }
        }
    }

    @Override
    public void scrollCallback(float yoffset) {
        world.getPlayer().scroll(yoffset);
    }
}
