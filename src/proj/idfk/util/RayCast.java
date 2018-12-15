package proj.idfk.util;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import proj.idfk.Application;
import proj.idfk.Camera;
import proj.idfk.Window;
import proj.idfk.world.Chunk;
import proj.idfk.world.Constants;
import proj.idfk.world.World;

public class RayCast {
    private static final int RECURSION_COUNT = 600;
    private static final float RAY_RANGE = 600;

    private final Window window;
    private final Camera camera;
    private final World world;
    private Vector3f worldRay;
    private Vector3i selectedBlock;

    public RayCast(Application app, World world) {
        this.window = app.getWindow();
        this.camera = app.getCamera();
        this.world = world;
        this.worldRay = new Vector3f();
    }

    public Vector3i getSelectedBlock() {
        return this.selectedBlock;
    }

    public void update() {
        Vector4f clip = new Vector4f(window.getNDCPosition(), -1, 1);
        Vector4f eye = camera.getInverseProjectionMatrix().transform(clip);
        eye.setComponent(2, -1);
        eye.setComponent(3, 0);
        Vector4f temp = camera.getInverseViewMatrix().transform(eye);
        worldRay.set(temp.x, temp.y, temp.z);
        worldRay.normalize();
        if (intersectionInRange(0, RAY_RANGE, worldRay)) {
            selectedBlock = binarySearch(0, 0, RAY_RANGE, worldRay);
        } else {
            selectedBlock = null;
        }
    }

    private Vector3f getPointOnRay(Vector3f ray, float distance) {
        Vector3f start = camera.getPosition();
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return scaledRay.add(start);
    }

    private boolean intersectionInRange(float start, float finish, Vector3f ray) {
        Vector3f startPoint = getPointOnRay(ray, start);
        Vector3f endPoint = getPointOnRay(ray, finish);
        if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
            return true;
        } else {
            return false;
        }
    }

    private Vector3i binarySearch(int count, float start, float finish, Vector3f ray) {
        float half = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
            Vector3f endPoint = getPointOnRay(ray, half);
            Chunk ch = world.get(Math.round(endPoint.x()), Math.round(endPoint.z()));
            if (ch != null) {
                return new Vector3i(Math.round(endPoint.x), Math.round(endPoint.y), Math.round(endPoint.z));
            } else {
                return null;
            }
        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray);
        } else {
            return binarySearch(count + 1, half, finish, ray);
        }
    }

    private boolean isUnderGround(Vector3f testPoint) {
        Chunk chunk = getChunk(Math.round(testPoint.x), Math.round(testPoint.z));
        float height = 0;
        if (chunk != null) {
            height = chunk.getHeight(Math.round(testPoint.x) % Constants.CHUNK_SIZE, Math.round(testPoint.z) % Constants.CHUNK_SIZE);
        }
        if (testPoint.y < height) {
            return true;
        } else {
            return false;
        }
    }


    private Chunk getChunk(int worldX, int worldZ) {
        return world.get(worldX, worldZ);
    }
}
