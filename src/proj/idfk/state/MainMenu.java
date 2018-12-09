package proj.idfk.state;

import org.joml.Vector2i;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;
import proj.idfk.Application;
import proj.idfk.render.MasterRenderer;
import proj.idfk.render.NuklearRenderer;

import static org.lwjgl.nuklear.Nuklear.*;

public class MainMenu implements GameState {
    private static final float buttonRatio[] = {0.2f, 0.6f, 0.2f};
    private final Application app;

    public MainMenu(Application app) {
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
        try (MemoryStack stack = MemoryStack.stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            final NkContext ctx = renderer.getContext();
            final Vector2i size = app.getWindow().getWindowSize();
            ctx.style().button().text_background(NuklearRenderer.grey);
            NkStyleItem style = NkStyleItem.mallocStack(stack);
            ctx.style().window().fixed_background(nk_style_item_image(renderer.getBackground(), style));
            if (nk_begin(ctx, "MainMenu", nk_rect(0, 0, size.x, size.y, rect), NK_WINDOW_NO_SCROLLBAR)) {
                nk_layout_row_static(ctx, size.y / 4f, 15, 1);

                nk_layout_row(ctx, NK_DYNAMIC, size.y / 6f, buttonRatio);
                {
                    nk_spacing(ctx, 1);
                    if (nk_button_label(ctx, "Play")) {
                        app.getStateManager().push(GameStateManager.GameState.WorldSelector);
                    }
                    nk_spacing(ctx, 2);
                    if (nk_button_label(ctx, "Settings")) {
                        app.getStateManager().push(GameStateManager.GameState.Settings);
                    }
                    nk_spacing(ctx, 2);
                    if (nk_button_label(ctx, "Exit")) {
                        app.getWindow().setShouldClose();
                    }
                    nk_spacing(ctx, 1);
                }
            }
            nk_end(ctx);
        }
    }
}
