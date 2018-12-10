package proj.idfk.world;

public class World {
    private final String name;
    private final Long seed;

    public World(String name, Long seed) {
        this.name = name;
        this.seed = seed;
    }

    public String getName() {
        return this.name;
    }

    public Long getSeed() {
        return this.seed;
    }
}
