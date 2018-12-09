package proj.idfk;

import java.util.prefs.Preferences;

public class Config {
    public int width;
    public int height;
    public int fov;
    public int renderDistance;
    public float sensitivity;
    public boolean vsync;
    public boolean fullscreen;

    private final Preferences pref;

    public Config() {
        this.pref = Preferences.userNodeForPackage(Config.class);
        load();
    }

    private void load() {
        this.width = pref.getInt("width", 800);
        this.height = pref.getInt("height", 600);
        this.fov = pref.getInt("fov", 70);
        this.renderDistance = pref.getInt("renderDistance", 4);
        this.sensitivity = pref.getFloat("sensitivity", 0.5f);
        this.vsync = pref.getBoolean("vsync", true);
        this.fullscreen = pref.getBoolean("fullscreen", false);
    }

    public void save() {
        pref.putInt("width", width);
        pref.putInt("height", height);
        pref.putInt("fov", fov);
        pref.putInt("renderDistance", renderDistance);
        pref.putFloat("sensitivity", sensitivity);
        pref.putBoolean("vsync", vsync);
        pref.putBoolean("fullscreen", fullscreen);
    }

    public Config parseArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-width":
                case "-w":
                    this.width = Integer.valueOf(args[++i]);
                    break;
                case "-height":
                case "-h":
                    this.height = Integer.valueOf(args[++i]);
                    break;
                case "-fov":
                    this.fov = Integer.valueOf(args[++i]);
                    break;
                case "-renderDistance":
                    this.renderDistance = Integer.valueOf(args[++i]);
                    break;
                case "-sensitivity":
                    this.sensitivity = Float.valueOf(args[++i]);
                    break;
                case "-vsync":
                    this.vsync = Integer.valueOf(args[++i]) >= 1;
                    break;
                case "-fullscreen":
                    this.fullscreen = Integer.valueOf(args[++i]) >= 1;
                    break;
            }
        }
        return this;
    }

    public Config print() {
        System.out.println("---CONFIG---");
        System.out.format("Width: %d\n" +
                "Height: %d\n" +
                "Fov: %d\n" +
                "RenderDistance: %d\n" +
                "Sensitivity: %.2f\n" +
                "Vsync: %b\n" +
                "Fullscreen: %b\n", width, height, fov, renderDistance, sensitivity, vsync, fullscreen);
        System.out.println("---CONFIG---");
        return this;
    }
}
