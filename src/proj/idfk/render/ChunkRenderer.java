package proj.idfk.render;

import proj.idfk.Camera;
import proj.idfk.shader.ChunkShader;
import proj.idfk.texture.TextureArray;
import proj.idfk.util.Disposable;
import proj.idfk.world.Chunk;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL45.*;

public class ChunkRenderer implements Disposable {
    private ChunkShader shader;
    private TextureArray blocksTexture;
    private List<Chunk> meshList;

    public ChunkRenderer() {
        this.shader = new ChunkShader();
        this.meshList = new ArrayList<>();
        this.blocksTexture = new TextureArray("textures/block/", 12);
        shader.loadBlockTextureArrayHandle(blocksTexture.getTextureHandle());
    }

    public void add(Chunk ch) {
        meshList.add(ch);
    }

    public void render(Camera camera) {
        shader.loadProjectionView(camera.getProjectionViewMatrix());
        shader.bind();
        for (Chunk chunk : meshList) {
            chunk.getMesh().bind();
            glDrawElements(GL_TRIANGLES, chunk.getMesh().getCount(), GL_UNSIGNED_INT, 0);
        }
        meshList.clear();
    }

    @Override
    public void dispose() {
        blocksTexture.dispose();
        shader.dispose();
    }
}
