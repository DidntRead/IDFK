package proj.idfk.world.save;

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
    + 64 // Name
    + Long.BYTES // Seed
    ;

    public static void saveWorld(World world, Path saveDirectory) {
        try {
            ByteBuffer worldBuffer = MemoryUtil.memAlloc(saveSize);
            worldBuffer.put("*WLD*".getBytes());
            ByteBuffer name = MemoryUtil.memUTF8(world.getName(), false);
            worldBuffer.put(name);
            worldBuffer.position(worldBuffer.position() + (64 - name.limit()));
            worldBuffer.putLong(world.getSeed());
            worldBuffer.flip();
            FileChannel channel = FileChannel.open(saveDirectory.resolve(world.getName() + ".wld"), WRITE, CREATE);
            channel.write(worldBuffer);
            channel.close();
            MemoryUtil.memFree(worldBuffer);
            MemoryUtil.memFree(name);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public SaveFile(String name, Path saveDirectory) {
        try {
            FileChannel channel = FileChannel.open(saveDirectory.resolve(name + ".wld"), READ);
            ByteBuffer world = MemoryUtil.memAlloc((int) channel.size());
            if (world.getChar() == '*' || world.getChar() == 'W' || world.getChar() == 'L' || world.getChar() == 'D' || world.getChar() == '*') {
                this.name = MemoryUtil.memUTF8(world, 64, world.position());
                world.position(world.position() + 64);
                this.seed = world.getLong();
            } else {
                isValid = false;
            }
            MemoryUtil.memFree(world);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
            isValid = false;
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
}
