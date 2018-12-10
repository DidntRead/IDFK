package proj.idfk.world.save;

import org.lwjgl.system.Platform;
import proj.idfk.world.World;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.CRC32C;

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

    public void removeWorld(int index) {
        try {
            Files.deleteIfExists(saveDirectory.resolve(worlds.get(index) + ".wld"));
            worlds.remove(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadWorld(int index) {
        SaveFile saveFile = new SaveFile(worlds.get(index), saveDirectory);
        if (saveFile.isValid()) {
            this.current = new World(saveFile.getName(), saveFile.getSeed());
            return true;
        } else {
            return false;
        }
    }

    public void newWorld(String name, String seed) {
        worlds.add(name);
        long seedLong;
        if (seed.length() != 0) {
            CRC32C crc = new CRC32C();
            crc.update(seed.getBytes());
            seedLong = crc.getValue();
        } else {
            seedLong = ThreadLocalRandom.current().nextLong();
        }
        current = new World(name, seedLong);
        SaveFile.saveWorld(current, saveDirectory);
    }

    public void saveCurrent() {
        if (current != null) {
            SaveFile.saveWorld(current, saveDirectory);
        }
        current = null;
    }

    public World getCurrentWorld() {
        return this.current;
    }
}
