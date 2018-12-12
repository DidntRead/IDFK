package proj.idfk.world.save;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import proj.idfk.world.World;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

public class SaveFile {
    private boolean isValid;
    private String name;
    private Long seed;
    private static final int saveSize = 5 //HEADER
    + 65 // Name
    + Long.BYTES // Seed
    + 3 * Float.BYTES // Player position
            ;
    private Vector3f playerPosition;

    public SaveFile(String name, Path saveDirectory) {
        try {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FileChannel channel = FileChannel.open(saveDirectory.resolve(name + ".wld"), READ);
                ByteBuffer world = stack.calloc((int) channel.size());
                channel.read(world);
                world.position(0);
                if (world.get() == '*' || world.get() == 'W' || world.get() == 'L' || world.get() == 'D' || world.get() == '*') {
                    this.name = MemoryUtil.memUTF8(world, 64, 5).split("\0")[0];
                    world.position(70);
                    this.seed = world.getLong();
                    this.playerPosition = new Vector3f(world.getFloat(), world.getFloat(), world.getFloat());
                    isValid = true;
                } else {
                    isValid = false;
                }
                channel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            isValid = false;
        }
    }

    public static void saveWorld(World world, Path saveDirectory) {
        try {
            FileChannel channel = FileChannel.open(saveDirectory.resolve(world.getName() + ".wld"), WRITE, CREATE, TRUNCATE_EXISTING);
            ByteBuffer worldBuffer = MemoryUtil.memAlloc(saveSize);
            worldBuffer.put("*WLD*".getBytes());
            MemoryUtil.memUTF8(world.getName(), true, worldBuffer, 5);
            worldBuffer.position(70);
            worldBuffer.putLong(world.getSeed());
            worldBuffer.putFloat(world.getPlayer().position.x);
            worldBuffer.putFloat(world.getPlayer().position.y);
            worldBuffer.putFloat(world.getPlayer().position.z);
            worldBuffer.flip();
            channel.write(worldBuffer);
            channel.close();
            MemoryUtil.memFree(worldBuffer);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public boolean isValid() {
        return this.isValid;
    }

    public String getName() {
        return this.name;
    }

    public Long getSeed() {
        return this.seed;
    }

    public Vector3f getPlayerPosition() {
        return this.playerPosition;
    }
}
