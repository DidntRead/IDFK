package proj.idfk.render;

import proj.idfk.Camera;
import proj.idfk.shader.ChunkShader;
import proj.idfk.util.Disposable;
import proj.idfk.util.VectorXZ;
import proj.idfk.world.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL45.*;

public class ChunkRenderer implements Disposable {
    private ChunkShader shader;
    private List<Chunk> meshList;

    public ChunkRenderer() {
        this.shader = new ChunkShader();
        this.meshList = new ArrayList<>();
    }

    public void add(Chunk ch) {
        meshList.add(ch);
    }

    public void add(Map<VectorXZ, Chunk> chunkMap) {
        meshList.addAll(chunkMap.values());
    }

    public void render(Camera camera) {
        System.out.println("CHUNK SIZE: " + meshList.size());
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
        shader.dispose();
    }
}
