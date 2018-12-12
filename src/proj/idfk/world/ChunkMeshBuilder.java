package proj.idfk.world;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import proj.idfk.util.VectorXZ;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static proj.idfk.world.Constants.CHUNK_HEIGHT;
import static proj.idfk.world.Constants.CHUNK_SIZE;

public class ChunkMeshBuilder {
    // Greedy meshing
    public static final int SOUTH      = 0;
    public static final int NORTH      = 1;
    public static final int EAST       = 2;
    public static final int WEST       = 3;
    public static final int TOP        = 4;
    public static final int BOTTOM     = 5;
    private Chunk chunk;

    public ChunkMeshBuilder(Chunk chunk) {
        this.chunk = chunk;
    }

    public void build() {
        greedy();
    }

    // GREEDY MESHING
    private void greedy() {
            FloatBuffer vertexBuf = MemoryUtil.memAllocFloat((48 * 6) * Constants.CHUNK_VOLUME);
            IntBuffer indexBuf = MemoryUtil.memAllocInt((24 * 6) * Constants.CHUNK_VOLUME);
            FloatBuffer textureBuf = MemoryUtil.memAllocFloat(24 * Constants.CHUNK_VOLUME);
            final VectorXZ position = this.chunk.getPosition();
            int index = 0;
            /*
             * These are just working variables for the algorithm - almost all taken
             * directly from Mikola Lysenko's javascript implementation.
             */
            int i, j, k, l, w, h, u, v, n, side = 0;

            final int[] x = new int[]{0, 0, 0};
            final int[] q = new int[]{0, 0, 0};
            final int[] du = new int[]{0, 0, 0};
            final int[] dv = new int[]{0, 0, 0};

            /*
             * We create a mask - this will contain the groups of matching voxel faces
             * as we proceed through the chunk in 6 directions - once for each face.
             */
            final VoxelFace[] mask = new VoxelFace[CHUNK_SIZE * CHUNK_HEIGHT];

            /*
             * These are just working variables to hold two faces during comparison.
             */
            VoxelFace voxelFace, voxelFace1;

            /**
             * We start with the lesser-spotted boolean for-loop (also known as the old flippy floppy).
             *
             * The variable backFace will be TRUE on the first iteration and FALSE on the second - this allows
             * us to track which direction the indices should run during creation of the quad.
             *
             * This loop runs twice, and the inner loop 3 times - totally 6 iterations - one for each
             * voxel face.
             */
            for (boolean backFace = true, b = false; b != backFace; backFace = backFace && b, b = !b) {

                /*
                 * We sweep over the 3 dimensions - most of what follows is well described by Mikola Lysenko
                 * in his post - and is ported from his Javascript implementation.  Where this implementation
                 * diverges, I've added commentary.
                 */
                for (int d = 0; d < 3; d++) {

                    u = (d + 1) % 3;
                    v = (d + 2) % 3;

                    x[0] = 0;
                    x[1] = 0;
                    x[2] = 0;

                    q[0] = 0;
                    q[1] = 0;
                    q[2] = 0;
                    q[d] = 1;

                    /*
                     * Here we're keeping track of the side that we're meshing.
                     */
                    if (d == 0) {
                        side = backFace ? WEST : EAST;
                    } else if (d == 1) {
                        side = backFace ? BOTTOM : TOP;
                    } else if (d == 2) {
                        side = backFace ? SOUTH : NORTH;
                    }

                    /*
                     * We move through the dimension from front to back
                     */
                    for (x[d] = -1; x[d] < CHUNK_SIZE; ) {

                        /*
                         * -------------------------------------------------------------------
                         *   We compute the mask
                         * -------------------------------------------------------------------
                         */
                        n = 0;

                        for (x[v] = 0; x[v] < CHUNK_HEIGHT; x[v]++) {

                            for (x[u] = 0; x[u] < CHUNK_SIZE; x[u]++) {

                                /*
                                 * Here we retrieve two voxel faces for comparison.
                                 */
                                voxelFace = (x[d] >= 0) ? getVoxelFace(x[0], x[1], x[2], side) : null;
                                voxelFace1 = (x[d] < CHUNK_SIZE - 1) ? getVoxelFace(x[0] + q[0], x[1] + q[1], x[2] + q[2], side) : null;

                                /*
                                 * Note that we're using the equals function in the voxel face class here, which lets the faces
                                 * be compared based on any number of attributes.
                                 *
                                 * Also, we choose the face to add to the mask depending on whether we're moving through on a backface or not.
                                 */
                                mask[n++] = ((voxelFace != null && voxelFace1 != null && voxelFace.equals(voxelFace1)))
                                        ? null
                                        : backFace ? voxelFace1 : voxelFace;
                            }
                        }

                        x[d]++;

                        /*
                         * Now we generate the mesh for the mask
                         */
                        n = 0;

                        for (j = 0; j < CHUNK_HEIGHT; j++) {

                            for (i = 0; i < CHUNK_SIZE; ) {

                                if (mask[n] != null) {

                                    /*
                                     * We compute the width
                                     */
                                    for (w = 1; i + w < CHUNK_SIZE && mask[n + w] != null && mask[n + w].equals(mask[n]); w++) {
                                    }

                                    /*
                                     * Then we compute height
                                     */
                                    boolean done = false;

                                    for (h = 1; j + h < CHUNK_HEIGHT; h++) {

                                        for (k = 0; k < w; k++) {

                                            if (mask[n + k + h * CHUNK_SIZE] == null || !mask[n + k + h * CHUNK_SIZE].equals(mask[n])) {
                                                done = true;
                                                break;
                                            }
                                        }

                                        if (done) {
                                            break;
                                        }
                                    }

                                    /*
                                     * Here we check the "transparent" attribute in the VoxelFace class to ensure that we don't mesh
                                     * any culled faces.
                                     */
                                    if (!mask[n].transparent) {
                                        /*
                                         * Add quad
                                         */
                                        x[u] = i;
                                        x[v] = j;

                                        du[0] = 0;
                                        du[1] = 0;
                                        du[2] = 0;
                                        du[u] = w;

                                        dv[0] = 0;
                                        dv[1] = 0;
                                        dv[2] = 0;
                                        dv[v] = h;

                                        final int textureIndex = BlockID.getTextureIndex(mask[n].type, mask[n].side);
                                        textureBuf.put(new float[] {textureIndex, textureIndex, textureIndex, textureIndex});

                                        // QUAD
                                        // BOTTOM LEFT
                                        vertexBuf.put(x[0] + position.x * CHUNK_SIZE);
                                        vertexBuf.put(x[1]);
                                        vertexBuf.put(x[2] + position.z * CHUNK_SIZE);

                                        // BOTTOM RIGHT
                                        vertexBuf.put(x[0] + dv[0] + position.x * CHUNK_SIZE);
                                        vertexBuf.put(x[1] + dv[1]);
                                        vertexBuf.put(x[2] + dv[2] + position.z * CHUNK_SIZE);

                                        // TOP LEFT
                                        vertexBuf.put(x[0] + du[0] + position.x * CHUNK_SIZE);
                                        vertexBuf.put(x[1] + du[1]);
                                        vertexBuf.put(x[2] + du[2] + position.z * CHUNK_SIZE);

                                        // TOP RIGHT
                                        vertexBuf.put(x[0] + du[0] + dv[0] + position.x * CHUNK_SIZE);
                                        vertexBuf.put(x[1] + du[1] + dv[1]);
                                        vertexBuf.put(x[2] + du[2] + dv[2] + position.z * CHUNK_SIZE);

                                        if (backFace) {
                                            indexBuf.put(2 + index);
                                            indexBuf.put(index);
                                            indexBuf.put(1 + index);
                                            indexBuf.put(1 + index);
                                            indexBuf.put(3 + index);
                                            indexBuf.put(2 + index);
                                        } else {
                                            indexBuf.put(2 + index);
                                            indexBuf.put(3 + index);
                                            indexBuf.put(1 + index);
                                            indexBuf.put(1 + index);
                                            indexBuf.put(index);
                                            indexBuf.put(2 + index);
                                        }
                                        index += 4;
                                    }
                                        /*
                                         * We zero out the mask
                                         */
                                        for (l = 0; l < h; ++l) {

                                            for (k = 0; k < w; ++k) {
                                                mask[n + k + l * CHUNK_SIZE] = null;
                                            }
                                        }

                                        /*
                                         * And then finally increment the counters and continue
                                         */
                                        i += w;
                                        n += w;

                                    } else {

                                        i++;
                                        n++;
                                    }
                                }
                            }
                        }
                    }
                }
                int vertSize = vertexBuf.position();
                int texSize = textureBuf.position();
                int elSize = indexBuf.position();
                vertexBuf.position(0);
                textureBuf.position(0);
                indexBuf.position(0);
                FloatBuffer vertexPositions = MemoryUtil.memSlice(vertexBuf, 0, vertSize);
                FloatBuffer textureCoordinates = MemoryUtil.memSlice(textureBuf, 0, texSize);
                IntBuffer indices = MemoryUtil.memSlice(indexBuf, 0, elSize);
                chunk.getMeshWithoutBuilding().uploadData(vertexPositions, textureCoordinates, indices);
                MemoryUtil.memFree(vertexBuf);
                MemoryUtil.memFree(textureBuf);
                MemoryUtil.memFree(indexBuf);
        }

    private VoxelFace getVoxelFace(final int x, final int y, final int z, final int side) {
        VoxelFace face = new VoxelFace();

        face.side = side;
        face.type = this.chunk.getBlock(x, y, z);

        if (this.chunk.getBlock(x, y, z) == BlockID.AIR) {
            face.transparent = true;
        } else {
            switch (side) {
                case TOP:
                    face.transparent = y != (CHUNK_HEIGHT - 1) && this.chunk.getBlock(x, y + 1, z) != BlockID.AIR;
                    break;
                case BOTTOM:
                    face.transparent = y != 0 && chunk.getBlock(x, y - 1, z) != BlockID.AIR;
                    break;
                case NORTH:
                    face.transparent = z != (Constants.CHUNK_SIZE - 1) && chunk.getBlock(x, y, z + 1) != BlockID.AIR;
                    break;
                case SOUTH:
                    face.transparent = z != 0 && chunk.getBlock(x, y, z - 1) != BlockID.AIR;
                    break;
                case EAST:
                    face.transparent = x != (Constants.CHUNK_SIZE - 1) && chunk.getBlock(x + 1, y, z) != BlockID.AIR;
                    break;
                case WEST:
                    face.transparent = x != 0 && chunk.getBlock(x - 1, y, z) != BlockID.AIR;
                    break;
            }
        }

        return face;
    }

    private class VoxelFace {

        public boolean transparent;
        public byte type;
        public int side;

        public boolean equals(final VoxelFace face) { return face.transparent == this.transparent && face.type == this.type; }
    }
}
