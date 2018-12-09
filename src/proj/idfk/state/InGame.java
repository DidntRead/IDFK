package proj.idfk.state;

import proj.idfk.Application;
import proj.idfk.callback.KeyCallback;
import proj.idfk.render.MasterRenderer;
import proj.idfk.world.SaveManager;
import proj.idfk.world.World;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class InGame implements GameState, KeyCallback {
    private final Application app;
    private World world = null;

    public InGame(Application app, SaveManager saveManager) {
        this.app = app;
    }

    @Override
    public void on_enter() {

    }

    @Override
    public void on_exit() {

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(MasterRenderer renderer) {

    }

    @Override
    public void keyCallback(int key, int action, int modifiers) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
            app.getStateManager().push(GameStateManager.GameState.PauseMenu);
        }
    }
}
