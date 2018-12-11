package proj.idfk.render;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkImage;
import proj.idfk.Application;
import proj.idfk.Camera;
import proj.idfk.util.Disposable;
import proj.idfk.util.VectorXZ;
import proj.idfk.world.Chunk;

import java.util.Map;

import static org.lwjgl.opengl.GL45.*;

public class MasterRenderer implements Disposable {
    private final NuklearRenderer nuklearRenderer;
    private final ChunkRenderer chunkRenderer;
    private boolean inGame = false;

    public MasterRenderer(Application app) {
        this.nuklearRenderer = new NuklearRenderer(app);
        this.chunkRenderer = new ChunkRenderer();
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_SCISSOR_TEST);
    }

    public void finish(Camera camera) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        if (inGame) {
            chunkRenderer.render(camera);
        } else {
            nuklearRenderer.render();
        }
    }

    public void add(Chunk ch) {
        chunkRenderer.add(ch);
    }

    public void add(Map<VectorXZ, Chunk> chunkMap) {
        chunkRenderer.add(chunkMap);
    }

    public void beginInput() {
        nuklearRenderer.beginInput();
    }

    public void endInput() {
        nuklearRenderer.endInput();
    }

    public void setInGame(boolean v) {
        this.inGame = v;
        if (v) {
            glDisable(GL_BLEND);
            glDisable(GL_SCISSOR_TEST);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
        } else {
            glEnable(GL_BLEND);
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            glEnable(GL_SCISSOR_TEST);
        }
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
        chunkRenderer.dispose();
    }
}
