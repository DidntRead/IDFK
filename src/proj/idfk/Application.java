package proj.idfk;

import proj.idfk.render.MasterRenderer;
import proj.idfk.state.GameStateManager;
import proj.idfk.util.Disposable;
import proj.idfk.util.Timer;

import java.lang.reflect.Field;

public class Application implements Disposable {
    private boolean debug;
    private String name;
    private Config config;
    private Window window;
    private Timer deltaTimer;
    private GameStateManager stateManager;
    private MasterRenderer renderer;

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
        this.deltaTimer = new Timer();
        this.stateManager = new GameStateManager(this);
        this.renderer = new MasterRenderer();
    }

    public void mainLoop() {
        while (!window.shouldClose() && !stateManager.isEmpty()) {
            float delta = deltaTimer.elapsed();
            measureDelta(delta);

            window.poll();

            stateManager.update(delta);

            stateManager.render(renderer);

            window.display();

            stateManager.signal();
        }
    }

    private float averageDelta;
    private float maxDelta = Float.MIN_VALUE;
    private float minDelta = Float.MAX_VALUE;
    private float updateTimer = 750f;

    private void measureDelta(float delta) {
        if (debug) {
            delta *= 1000f;

            updateTimer -= delta;

            if (delta > maxDelta) {
                maxDelta = delta;
            } else if (delta < minDelta) {
                minDelta = delta;
            }

            averageDelta += delta;
            averageDelta /= 2f;

            if (updateTimer < 0) {
                updateTimer = 750f;
                window.setTitle(name + String.format(" - FPS: %.2f, Average: %.2f, Min: %.2f, Max: %.2f", 1000 / averageDelta, averageDelta, minDelta, maxDelta));
            }
        }
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

    @Override
    public void dispose() {
        window.dispose();
        config.save();
    }
}
