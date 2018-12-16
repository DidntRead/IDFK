package proj.idfk.world.generation;

import proj.idfk.world.BlockID;
import proj.idfk.world.Chunk;
import proj.idfk.world.Constants;

public class FlatGenerator implements ChunkGenerator {
    @Override
    public void generate(Chunk chunk) {
        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int z = 0; z < Constants.CHUNK_SIZE; z++) {
                chunk.setBlock(x, 0, z, BlockID.BEDROCK);
                chunk.setBlock(x, 1, z, BlockID.DIRT);
                chunk.setBlock(x, 2, z, BlockID.DIRT);
                chunk.setBlock(x, 3, z, BlockID.GRASS);
            }
        }
    }
}
