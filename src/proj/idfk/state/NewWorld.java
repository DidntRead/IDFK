package proj.idfk.state;

import org.joml.Vector2i;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import proj.idfk.Application;
import proj.idfk.callback.KeyCallback;
import proj.idfk.render.MasterRenderer;
import proj.idfk.world.save.SaveManager;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.nuklear.Nuklear.*;

public class NewWorld implements GameState, KeyCallback {
    private final Application app;
    private final SaveManager saveManager;
    private final ByteBuffer seed;
    private final ByteBuffer name;
    private final IntBuffer seedLength;
    private final IntBuffer nameLength;

    public NewWorld(Application app, SaveManager saveManager) {
        this.app = app;
        this.saveManager = saveManager;
        this.seed = MemoryUtil.memCalloc(65);
        this.name = MemoryUtil.memCalloc(65);
        this.seedLength = MemoryUtil.memAllocInt(1);
        this.nameLength = MemoryUtil.memAllocInt(1);
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
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final NkContext ctx = renderer.getContext();
            final Vector2i size = app.getWindow().getWindowSize();

            final float[] ratio = {size.x / 10f, size.x - 2 * (size.x / 10f), size.x / 10f};
            final float[] ratio2 = {size.x / 10f, size.x / 10f, size.x - 3 * (size.x / 10f), size.x / 10f};

            NkRect rect = NkRect.mallocStack(stack);
            nk_begin(ctx, "NewWorld", nk_rect(0, 0, size.x, size.y, rect), NK_WINDOW_NO_SCROLLBAR);
            {
                nk_layout_row_static(ctx, size.y / 4f, 15, 1);

                nk_layout_row(ctx, NK_STATIC, size.y / 10f, ratio2);
                {
                    nk_spacing(ctx, 1);
                    nk_label(ctx, "Name: ", NK_TEXT_LEFT);
                    nk_edit_string(ctx, NK_EDIT_FIELD, name, nameLength, 64, null);
                    nk_spacing(ctx, 2);
                    nk_label(ctx, "Seed: ", NK_TEXT_LEFT);
                    nk_edit_string(ctx, NK_EDIT_FIELD, seed, seedLength, 64, null);
                    nk_spacing(ctx, 1);
                }

                nk_layout_row(ctx, NK_STATIC, size.y / 10f, ratio);
                {
                    nk_spacing(ctx, 1);
                    if (nk_button_label(ctx, "Create")) {
                        saveManager.newWorld(MemoryUtil.memASCII(name, nameLength.get(0)), MemoryUtil.memASCII(seed, seedLength.get(0)));
                        app.getStateManager().push(GameStateManager.GameState.InGame);
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
