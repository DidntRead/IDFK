package proj.idfk.world.save;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import proj.idfk.util.VectorXZ;
import proj.idfk.world.World;
import proj.idfk.world.event.PlayerDigEvent;

import java.io.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardOpenOption.*;

public class SaveFile {
    private boolean isValid;
    private String name;
    private Map<VectorXZ, List<PlayerDigEvent>> events;
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
                    ObjectInputStream in = new ObjectInputStream(new InputStream() {
                        @Override
                        public int read(byte[] b, int off, int len) {
                            if (len <= 0) {
                                return 0;
                            }
                            for (int x = off; x < off + len; x++) {
                                b[x] = world.get();
                                if (world.position() > world.limit()) {
                                    return x - off;
                                }
                            }
                            return len;
                        }

                        @Override
                        public int read(byte[] b) throws IOException, NullPointerException {
                            if (b == null) {
                                throw new NullPointerException();
                            }
                            if (world.position() > world.limit()) {
                                return  -1;
                            }
                            if (b.length <= 0) {
                                return 0;
                            }
                            for (int i = 0; i < b.length; i++) {
                                b[i] = world.get();
                                if (world.position() > world.limit()) {
                                    return i;
                                }
                            }
                            return b.length;
                        }

                        @Override
                        public int read() throws IOException {
                            if (world.position() > world.limit()) {
                                return -1;
                            }
                            return world.get();
                        }
                    });
                    try {
                        events = (Map<VectorXZ, List<PlayerDigEvent>>) in.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
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
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOutput);
            out.writeObject(world.getEventMap());
            ByteBuffer worldBuffer = MemoryUtil.memAlloc(saveSize + byteOutput.size());
            worldBuffer.put("*WLD*".getBytes());
            MemoryUtil.memUTF8(world.getName(), true, worldBuffer, 5);
            worldBuffer.position(70);
            worldBuffer.putLong(world.getSeed());
            worldBuffer.putFloat(world.getPlayer().position.x);
            worldBuffer.putFloat(world.getPlayer().position.y);
            worldBuffer.putFloat(world.getPlayer().position.z);
            OutputStream stream = new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    worldBuffer.put((byte) b);
                }
            };
            byteOutput.writeTo(stream);
            byteOutput.close();
            out.close();
            stream.close();
            //out.writeObject(world.getEventMap());
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

    public Map<VectorXZ, List<PlayerDigEvent>> getEvents() {
        return this.events;
    }
}
