package proj.idfk.world;

import static proj.idfk.world.ChunkMeshBuilder.*;

public class BlockID {
    public static final byte BEDROCK = -1;
    public static final byte AIR = 0;
    public static final byte DIRT = 1;
    public static final byte GRASS = 2;
    public static final byte STONE = 3;
    public static final byte SAND = 4;
    public static final byte CACTUS = 5;
    public static final byte CRAFTING_TABLE = 6;

    private static final BlockData bedrock = new BlockData(true);
    private static final BlockData dirt = new BlockData(true);
    private static final BlockData grass = new BlockData(true);
    private static final BlockData stone = new BlockData(true);
    private static final BlockData sand = new BlockData(true);
    private static final BlockData cactus = new BlockData(true);
    private static final BlockData craftingTable = new BlockData(true);

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
            case SAND:
                return sand;
            case CACTUS:
                return cactus;
            case CRAFTING_TABLE:
                return craftingTable;
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
                    case TOP:
                        return 3;
                    case ChunkMeshBuilder.BOTTOM:
                        return 1;
                    default:
                        return 2;
                }
            case CACTUS:
                switch (side) {
                    case TOP:
                    case ChunkMeshBuilder.BOTTOM:
                        return 8;
                    default:
                        return 7;
                }
            case CRAFTING_TABLE:
                switch (side) {
                    case TOP:
                    case BOTTOM:
                        return 11;
                    case SOUTH:
                        return 10;
                    default:
                        return 9;
                }
            case SAND:
                return 6;
            case STONE:
                return 4;
            case BEDROCK:
                return 5;
            default:
                return 0;
        }
    }
}
