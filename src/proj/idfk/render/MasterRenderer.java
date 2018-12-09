package proj.idfk.render;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkImage;
import proj.idfk.Application;
import proj.idfk.util.Disposable;

import static org.lwjgl.opengl.GL45.*;

public class MasterRenderer implements Disposable {
    private NuklearRenderer nuklearRenderer;

    public MasterRenderer(Application app) {
        this.nuklearRenderer = new NuklearRenderer(app);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void finish() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_SCISSOR_TEST);
        nuklearRenderer.render();
        glDisable(GL_BLEND);
        glDisable(GL_SCISSOR_TEST);
    }

    public void beginInput() {
        nuklearRenderer.beginInput();
    }

    public void endInput() {
        nuklearRenderer.endInput();
    }

    public NkContext getContext() {
        return nuklearRenderer.getContext();
    }

    public NkImage getBackground() {
        return nuklearRenderer.getBackground();
    }

    @Override
    public void dispose() {
        nuklearRenderer.dispose();
    }
}
