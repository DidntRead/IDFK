package proj.idfk.util;

public class Resolution {
    final public int width, height;

    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(Resolution.class.isAssignableFrom(obj.getClass()))) {
            return false;
        }

        Resolution res = (Resolution) obj;

        return res.width == this.width && res.height == this.height;
    }

    @Override
    public String toString() {
        return width+"x"+height;
    }
}
