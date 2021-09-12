package proj.idfk.world.generation;

import proj.idfk.util.FastNoise;
import proj.idfk.util.VectorXZ;
import proj.idfk.world.BlockID;
import proj.idfk.world.Chunk;
import proj.idfk.world.Constants;

public class NormalGenerator implements ChunkGenerator {
    private final FastNoise noise;

    public NormalGenerator(Long seed) {
        this.noise = new FastNoise(seed.intValue());
    }


    @Override
    public void generate(Chunk chunk) {
        final VectorXZ position = chunk.getPosition();
        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int z = 0; z < Constants.CHUNK_SIZE; z++) {
                int height = (int)((noise.GetSimplex(x + (position.x * Constants.CHUNK_SIZE), z + (position.z * Constants.CHUNK_SIZE)) + 1.5f) * Constants.CHUNK_GENERATION_VALUE);
                chunk.setBlock(x, height, z, BlockID.GRASS);
                chunk.setBlock(x, height - 1, z, BlockID.DIRT);
                chunk.setBlock(x, height - 2, z, BlockID.DIRT);
                for (int i = height - 3; i > 0; i--) {
                    chunk.setBlock(x, i, z, BlockID.STONE);
                }
                chunk.setBlock(x, 0, z, BlockID.BEDROCK);
            }
        }
    }
}
