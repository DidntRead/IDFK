package proj.idfk.state;

import proj.idfk.Application;
import proj.idfk.Camera;
import proj.idfk.callback.KeyCallback;
import proj.idfk.render.MasterRenderer;
import proj.idfk.world.save.SaveManager;
import proj.idfk.world.World;

import static org.lwjgl.glfw.GLFW.*;

public class InGame implements GameState, KeyCallback {
    private final Application app;
    private final SaveManager saveManager;
    private World world = null;

    public InGame(Application app, SaveManager saveManager) {
        this.app = app;
        this.saveManager = saveManager;
    }

    @Override
    public void on_enter() {
        this.world = saveManager.getCurrentWorld();
        app.getWindow().disableNuklearInput();
        app.getCamera().hookEntity(world.getPlayer());
        app.getRenderer().setInGame(true);
        this.world = saveManager.getCurrentWorld();
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
    }

    @Override
    public void render(MasterRenderer renderer, Camera camera) {
        world.getPlayer().render();
        world.render(app, camera);
    }

    @Override
    public void keyCallback(int key, int action, int modifiers) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
            app.getStateManager().push(GameStateManager.GameState.PauseMenu);
        }
    }
}
