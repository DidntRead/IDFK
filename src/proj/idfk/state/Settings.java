package proj.idfk.state;

import org.joml.Vector2i;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.system.MemoryStack;
import proj.idfk.Application;
import proj.idfk.Config;
import proj.idfk.callback.KeyCallback;
import proj.idfk.render.MasterRenderer;
import proj.idfk.util.Resolution;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.nuklear.Nuklear.*;

public class Settings implements GameState, KeyCallback {
    private final Application app;

    public Settings(Application app) {
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
        final NkContext ctx = renderer.getContext();
        final Config config = app.getConfig();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            final Vector2i size = app.getWindow().getWindowSize();
            nk_begin(ctx, "Settings", nk_rect(0, 0, size.x, size.y, rect), 0);
            {
                nk_layout_row_static(ctx, size.y / 2f, (int) (size.x * 0.6f), 2);

                float[] ratio = {0.2f, 0.6f, 0.2f};
                final List<Resolution> resolutions = app.getWindow().getUsableResolutions();
                nk_layout_row(ctx, NK_DYNAMIC, 0, ratio);
                {
                    String renderDistance = "";
                    switch (config.renderDistance) {
                        case 4:
                            renderDistance = "Near";
                            break;
                        case 8:
                            renderDistance = "Medium";
                            break;
                        case 16:
                            renderDistance = "Far";
                            break;
                        case 32:
                            renderDistance = "Extreme";
                            break;
                    }
                    nk_spacing(ctx, 1);
                    if (nk_button_label(ctx, "RenderDistance: " + renderDistance)) {
                        config.renderDistance *= 2;
                        if (config.renderDistance > 32) {
                            config.renderDistance = 4;
                        }
                    }

                    nk_spacing(ctx, 1);

                    nk_label(ctx, "Resolution:", NK_TEXT_LEFT);
                    if (nk_combo_begin_label(ctx, size.x + "x" + size.y, NkVec2.mallocStack(stack).x(size.x * 0.6f).y(200))) {
                        nk_layout_row_dynamic(ctx, 25, 1);
                        for (Resolution res : resolutions) {
                            if (nk_combo_item_label(ctx, res.toString(), NK_TEXT_LEFT)) {
                                config.width = res.width;
                                config.height = res.height;
                            }
                        }
                        nk_combo_end(ctx);
                    }
                    nk_spacing(ctx, 1);
                    IntBuffer vsync = stack.mallocInt(1);
                    IntBuffer fullscreen = stack.mallocInt(1);
                    FloatBuffer sensitivity = stack.mallocFloat(1);
                    vsync.put(0, config.vsync ? 0 : 1);
                    fullscreen.put(0, config.fullscreen ? 0 : 1);
                    sensitivity.put(0, config.sensitivity);

                    nk_checkbox_label(ctx, "Vsync: ", vsync);
                    nk_checkbox_label(ctx, "Fullscreen: ", fullscreen);

                    nk_spacing(ctx, 1);
                    nk_label(ctx, "Sensitivity:", NK_TEXT_LEFT);
                    nk_slider_float(ctx, 0.05f, sensitivity, 3f, 0.05f);

                    config.fullscreen = fullscreen.get(0) == 0;
                    config.vsync = vsync.get(0) == 0;
                    config.sensitivity = sensitivity.get(0);

                    app.getWindow().applySettings(config);
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
