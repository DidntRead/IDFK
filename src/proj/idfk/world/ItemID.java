package proj.idfk.world;

public class ItemID {
    public static final int DIRT = 1;
    public static final int GRASS = 2;
    public static final int STONE = 3;
    public static final int COBBLESTONE = 4;

    public static int fromBlockID(byte block) {
        switch (block) {
            case BlockID.DIRT:
            case BlockID.GRASS:
                return DIRT;
            case BlockID.STONE:
                return COBBLESTONE;
        }
        return -1;
    }
}
