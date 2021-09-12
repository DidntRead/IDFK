package proj.idfk;

import proj.idfk.render.MasterRenderer;
import proj.idfk.state.GameStateManager;
import proj.idfk.util.Disposable;
import proj.idfk.util.Timer;
import proj.idfk.world.save.SaveManager;

import java.lang.reflect.Field;

public class Application implements Disposable {
    private boolean debug;
    private final String name;
    private final Config config;
    private final Window window;
    private final Timer deltaTimer;
    private final GameStateManager stateManager;
    private final MasterRenderer renderer;
    private final Camera camera;
    @SuppressWarnings("FieldCanBeLocal")
    private final SaveManager saveManager;

    private float[] delta = new float[10];

    private float updateTimer = -1f;
    private int index = 0;
    public Application(String name, Config config) {
        // Debug
        try {
            setupDebug(Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()));
        } catch (ClassNotFoundException e) {
            this.debug = false;
        }

        this.name = name;
        this.config = config;
        this.window = new Window(name, config, debug);
        this.renderer = new MasterRenderer(this);
        this.saveManager = new SaveManager(this);
        this.camera = new Camera(config);
        this.stateManager = new GameStateManager(this, saveManager);
        this.deltaTimer = new Timer();
    }
    private float maxDelta = Float.MIN_VALUE;
    private float minDelta = Float.MAX_VALUE;

    public void mainLoop() {
        while (!window.shouldClose() && !stateManager.isEmpty()) {
            float delta = deltaTimer.elapsed();
            measureDelta(delta);

            renderer.beginInput();
            window.poll();
            renderer.endInput();

            stateManager.update(delta);

            stateManager.render(renderer, camera);

            camera.update();

            renderer.finish(camera);

            window.display();

            stateManager.signal();
        }
    }

    private void measureDelta(float delta) {
        if (debug) {
            delta *= 1000f;

            updateTimer -= delta;

            if (delta > maxDelta) {
                maxDelta = delta;
            }
            if (delta < minDelta) {
                minDelta = delta;
            }

            if (index >= this.delta.length) {
                index = 0;
            }
            this.delta[index++] = delta;

            float averageDelta = average(this.delta);

            if (updateTimer < 0) {
                updateTimer = 1000f;
                window.setTitle(name + String.format(" - FPS: %.2f, Average: %.2f, Min: %.2f, Max: %.2f", 1000 / averageDelta, averageDelta, minDelta, maxDelta));
            }
        }
    }

    private float average(float[] arr) {
        float res = 0f;
        for (float f : arr) {
            res += f;
        }
        return res / arr.length;
    }

    private void setupDebug(Class cl) {
        try {
            Field debug = cl.getDeclaredField("debug");
            if (java.lang.reflect.Modifier.isStatic(debug.getModifiers())) {
                this.debug = debug.getBoolean(null);
            } else {
                this.debug = false;
            }
        } catch (NoSuchFieldException | IllegalAccessException noField) {
            this.debug = false;
        }
    }

    public Window getWindow() {
        return this.window;
    }

    public GameStateManager getStateManager() {
        return this.stateManager;
    }

    public Config getConfig() {
        return this.config;
    }

    public MasterRenderer getRenderer() {
        return this.renderer;
    }

    public Camera getCamera() {
        return this.camera;
    }

    @Override
    public void dispose() {
        renderer.dispose();
        window.dispose();
        config.save();
    }
}
