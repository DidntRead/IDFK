package proj.idfk.util;

public class VectorXZ {
    private int x;
    private int z;

    public VectorXZ(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public VectorXZ(int v) {
        this.x = v;
        this.z = v;
    }

    public VectorXZ() {
        this.x = 0;
        this.z = 0;
    }

    public int x() {
        return this.x;
    }

    public int z() {
        return this.z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = prime * hash + x;
        hash = prime * hash + z;
        return hash;
    }

    @Override
    public String toString() {
        return this.x + " " + this.z;
    }
}
