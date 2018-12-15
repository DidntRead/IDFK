package proj.idfk.state;

import proj.idfk.Application;
import proj.idfk.Camera;
import proj.idfk.callback.KeyCallback;
import proj.idfk.callback.MouseButtonCallback;
import proj.idfk.callback.ScrollCallback;
import proj.idfk.render.MasterRenderer;
import proj.idfk.util.RayCast;
import proj.idfk.world.BlockID;
import proj.idfk.world.save.SaveManager;
import proj.idfk.world.World;

import static org.lwjgl.glfw.GLFW.*;

public class InGame implements GameState, KeyCallback, ScrollCallback, MouseButtonCallback {
    private final Application app;
    private final SaveManager saveManager;
    private World world = null;
    private RayCast rayCast;

    public InGame(Application app, SaveManager saveManager) {
        this.app = app;
        this.saveManager = saveManager;
    }

    @Override
    public void on_enter() {
        this.world = saveManager.getCurrentWorld();
        this.rayCast = new RayCast(app, world);
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

        rayCast.update();
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

    @Override
    public void mouseButtonCallback(int x, int y, int button, boolean press) {
        if (button == GLFW_MOUSE_BUTTON_LEFT && press) {
            if (rayCast.getSelectedBlock() != null) {
                //System.out.format("X: %d, Y: %d, Z: %d\n", rayCast.getSelectedBlock().x, rayCast.getSelectedBlock().y, rayCast.getSelectedBlock().z);
                //world.setBlock(rayCast.getSelectedBlock(), BlockID.AIR);
            } else {
                System.out.println("Selected block empty");
            }
        }
    }
}
