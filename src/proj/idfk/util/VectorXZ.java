package proj.idfk.util;

import java.util.Vector;

public class VectorXZ {
    public int x;
    public int z;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!VectorXZ.class.isAssignableFrom(obj.getClass())) return false;
        if (obj == this) return true;

        final VectorXZ vec = (VectorXZ) obj;

        return vec.x == this.x && vec.z == this.z;
    }
}
