package proj.idfk.world;

public class BlockID {
    public static final byte BEDROCK = -1;
    public static final byte AIR = 0;
    public static final byte DIRT = 1;
    public static final byte GRASS = 2;
    public static final byte STONE = 3;

    private static final BlockData bedrock = new BlockData(true);
    private static final BlockData dirt = new BlockData(true);
    private static final BlockData grass = new BlockData(true);
    private static final BlockData stone = new BlockData(true);

    public static BlockData getBlockData(byte id) {
        switch (id) {
            case BEDROCK:
                return bedrock;
            case DIRT:
                return dirt;
            case GRASS:
                return grass;
            case STONE:
                return stone;
            default:
                return null;
        }
    }

    public static int getTextureIndex(byte id, int side) {
        switch (id) {
            case DIRT:
                return 1;
            case GRASS:
                switch (side) {
                    case ChunkMeshBuilder.TOP:
                        return 3;
                    case ChunkMeshBuilder.BOTTOM:
                        return 1;
                    default:
                        return 2;
                }
            case STONE:
                return 4;
            case BEDROCK:
                return 5;
            default:
                return 0;
        }
    }
}
