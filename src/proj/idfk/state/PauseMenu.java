package proj.idfk.state;

import proj.idfk.Application;
import proj.idfk.callback.KeyCallback;
import proj.idfk.render.MasterRenderer;
import proj.idfk.world.SaveManager;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class PauseMenu implements GameState, KeyCallback {
    private Application app;
    private SaveManager saveManager;

    public PauseMenu(Application app, SaveManager saveManager) {
        this.app = app;
        this.saveManager = saveManager;
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
            app.getStateManager().pop();
        }
    }
}
