package proj.idfk.state;

import org.joml.Vector2i;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;
import proj.idfk.Application;
import proj.idfk.Camera;
import proj.idfk.callback.KeyCallback;
import proj.idfk.render.MasterRenderer;
import proj.idfk.world.save.SaveManager;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.nuklear.Nuklear.*;

public class PauseMenu implements GameState, KeyCallback {
    private final Application app;
    private final SaveManager saveManager;

    public PauseMenu(Application app, SaveManager saveManager) {
        this.app = app;
        this.saveManager = saveManager;
    }

    //TODO Replace background with texture of game

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
    public void render(MasterRenderer renderer, Camera camera) {
        final NkContext ctx = renderer.getContext();
        final Vector2i size = app.getWindow().getWindowSize();

        final float[] buttonRatio = {size.x / 6f, size.x - (2 * (size.x / 6f)), size.x / 6f};

        try (MemoryStack stack = MemoryStack.stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            nk_begin(ctx, "Paused", nk_rect(0, 0, size.x, size.y, rect), NK_WINDOW_NO_SCROLLBAR);
            {
                nk_layout_row_static(ctx, size.y / 4f, 15, 1);
                nk_layout_row(ctx, NK_STATIC, size.y / 6f, buttonRatio);
                {
                    nk_spacing(ctx, 1);
                    if (nk_button_label(ctx, "Back to game")) {
                        app.getStateManager().pop();
                    }
                    nk_spacing(ctx, 2);
                    if (nk_button_label(ctx, "Settings")) {
                        app.getStateManager().push(GameStateManager.GameState.Settings);
                    }
                    nk_spacing(ctx, 2);
                    if (nk_button_label(ctx, "Save and exit")) {
                        saveManager.saveCurrent();
                        app.getStateManager().pop(GameStateManager.GameState.MainMenu);
                    }
                }
            }
            nk_end(ctx);
        }
    }

    @Override
    public void keyCallback(int key, int action, int modifiers) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
            app.getStateManager().pop();
        }
    }
}
