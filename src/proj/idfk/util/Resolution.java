package proj.idfk.util;

public enum Resolution {
    RES800x600(800, 600), RES1280x1024(1280, 1024), RES1366x768(1366, 768), RES1600x900(1600, 900), RES1920x1080(1920, 1080), RES2560x1440(2560, 1440), RES3840x2160(3840, 2160);

    final public int width, height;
    Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return width+"x"+height;
    }
}
