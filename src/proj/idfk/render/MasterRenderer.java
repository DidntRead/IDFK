package proj.idfk.render;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkImage;
import proj.idfk.Application;
import proj.idfk.Camera;
import proj.idfk.util.Disposable;

import static org.lwjgl.opengl.GL45.*;

public class MasterRenderer implements Disposable {
    private final NuklearRenderer nuklearRenderer;
    private final TestRenderer testRenderer;
    private boolean renderNuklear = true;

    public MasterRenderer(Application app) {
        this.nuklearRenderer = new NuklearRenderer(app);
        this.testRenderer = new TestRenderer();
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void finish(Camera camera) {
        glClear(GL_COLOR_BUFFER_BIT);
        if (renderNuklear) {
            glEnable(GL_BLEND);
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_SCISSOR_TEST);
            nuklearRenderer.render();
            glDisable(GL_BLEND);
            glDisable(GL_SCISSOR_TEST);
        } else {
            testRenderer.render(camera);
        }
    }

    public void beginInput() {
        nuklearRenderer.beginInput();
    }

    public void endInput() {
        nuklearRenderer.endInput();
    }

    public void setRenderNuklear(boolean v) {
        this.renderNuklear = v;
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
        testRenderer.dispose();
    }
}
