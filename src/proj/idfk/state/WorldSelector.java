package proj.idfk.state;

import org.joml.Vector2i;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkListView;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;
import proj.idfk.Application;
import proj.idfk.Camera;
import proj.idfk.callback.KeyCallback;
import proj.idfk.render.MasterRenderer;
import proj.idfk.world.save.SaveManager;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.nuklear.Nuklear.*;

public class WorldSelector implements GameState, KeyCallback {
    private final Application app;
    private final SaveManager saveManager;
    private int selected = 0;

    public WorldSelector(Application app, SaveManager saveManager) {
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
    public void render(MasterRenderer renderer, Camera camera) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final NkContext ctx = renderer.getContext();
            final Vector2i size = app.getWindow().getWindowSize();
            NkRect rect = NkRect.mallocStack(stack);

            float[] ratio = {30, size.x - 100f, 50};
            float[] ratio2 = {30, (size.x - 100f) / 2f, (size.x - 100f) / 2f, 50};
            List<String> worlds = saveManager.getWorlds();
            NkListView worldsList = NkListView.mallocStack(stack);

            nk_begin(ctx, "WorldSelector", nk_rect(0, 0, size.x, size.y, rect), NK_WINDOW_NO_SCROLLBAR);
            {
                nk_layout_row(ctx, NK_STATIC,size.y * 0.85f, ratio);
                {
                    nk_spacing(ctx, 1);
                    if (nk_list_view_begin(ctx, worldsList, "Worlds", NK_WINDOW_BORDER, 100, worlds.size())) {
                        nk_layout_row_dynamic(ctx, 100, 1);
                        {
                            for (int i = 0; i < worlds.size() - worldsList.begin(); i++) {
                                if (nk_button_label(ctx, worlds.get(worldsList.begin() + i))) {
                                    selected = i;
                                }
                            }
                        }
                        nk_list_view_end(worldsList);
                    }
                }

                nk_layout_row(ctx, NK_STATIC,size.y * 0.12f, ratio2);
                {
                        nk_spacing(ctx, 1);
                        if (nk_button_label(ctx, "Play")) {
                            if (saveManager.loadWorld(selected)) {
                                app.getStateManager().push(GameStateManager.GameState.InGame);
                            } else {
                                saveManager.removeWorld(selected);
                            }
                        }
                        if (nk_button_label(ctx, "New")) {
                            app.getStateManager().push(GameStateManager.GameState.NewWorld);
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
