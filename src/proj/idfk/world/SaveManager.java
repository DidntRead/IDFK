package proj.idfk.world;

import org.lwjgl.system.Platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    public final Path saveDirectory;
    private World current = null;

    List<String> worlds;

    public SaveManager() {
        switch (Platform.get()) {
            case WINDOWS:
                saveDirectory = Paths.get(System.getenv("APPDATA"), ".idfk", "saves");
                break;
            case LINUX:
                saveDirectory = Paths.get(System.getProperty("user.home"), ".idfk", "saves");
                break;
            default:
                System.out.println("Unsupported platform: " + Platform.get().getName());
                saveDirectory = null;
                return;
        }

        if (!Files.exists(saveDirectory)) {
            try {
                Files.createDirectories(saveDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        worlds = new ArrayList<>();
        try {
            Files.list(saveDirectory).forEach(path ->
                worlds.add(path.getFileName().toString().replace(".wld", ""))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getWorlds() {
        return this.worlds;
    }

    public void loadWorld(int index) {
        System.out.println("TODO IMPLEMENT");
    }

    public void newWorld(String name, String seed) {
        System.out.println("TODO IMPLEMENT");
    }

    public void saveCurrent() {
        current = null;
        System.out.println("TODO IMPLEMENT");
    }

    public World getCurrentWorld() {
        return this.current;
    }
}
