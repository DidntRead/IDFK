package proj.idfk.state;

import proj.idfk.Application;
import proj.idfk.Window;
import proj.idfk.render.MasterRenderer;
import proj.idfk.world.save.SaveManager;

import java.util.ArrayDeque;
import java.util.Deque;

public class GameStateManager {
    private boolean popState = false;
    private proj.idfk.state.GameState current;
    private proj.idfk.state.GameState desired = null;
    private final Deque<proj.idfk.state.GameState> stack;
    private final Window window;

    private final MainMenu mainMenu;
    private final Settings settings;
    private final WorldSelector worldSelector;
    private final NewWorld newWorld;
    private final PauseMenu pauseMenu;
    private final InGame inGame;

    public GameStateManager(Application app, SaveManager saveManager) {
        this.stack = new ArrayDeque<>();
        this.window = app.getWindow();

        this.mainMenu = new MainMenu(app);
        this.settings = new Settings(app);
        this.worldSelector = new WorldSelector(app, saveManager);
        this.newWorld = new NewWorld(app, saveManager);
        this.inGame = new InGame(app, saveManager);
        this.pauseMenu = new PauseMenu(app, saveManager);

        push(GameState.MainMenu);
        signal();
    }

    private proj.idfk.state.GameState instanceOf(GameState gameState) {
        switch (gameState) {
            case InGame:
                return inGame;
            case MainMenu:
                return mainMenu;
            case PauseMenu:
                return pauseMenu;
            case Settings:
                return settings;
            case WorldSelector:
                return worldSelector;
            case NewWorld:
                return newWorld;
        }
        System.out.println("Unknown game state: " + gameState.getClass().getSimpleName());
        return null;
    }

    public void update(float delta) {
        current.update(delta);
    }

    public void render(MasterRenderer renderer) {
        current.render(renderer);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @SuppressWarnings("ConstantConditions")
    public void push(GameState newState) {
        proj.idfk.state.GameState state = instanceOf(newState);
        proj.idfk.state.GameState old = stack.peek();
        if (old != null) {
            old.on_exit();
        }
        stack.push(state);
        state.on_enter();
        window.registerCallbacks(state);
        System.out.println("New game state: " + state.getClass().getSimpleName());
    }

    public void pop(GameState desired) {
        this.popState = true;
        this.desired = instanceOf(desired);
    }

    public void pop() {
        this.popState = true;
    }

    public void signal() {
        if (popState) {
            proj.idfk.state.GameState last = null;
            if (desired != null) {
                while (stack.peek() != desired) {
                    last = stack.pop();
                }
                desired = null;
            } else {
                last = stack.pop();
            }

            popState = false;
            proj.idfk.state.GameState newState = stack.peek();

            if (last != null) {
                last.on_exit();
            }

            if (newState != null) {
                newState.on_enter();
                window.registerCallbacks(newState);
                current = newState;
                assert stack.peek() != null;
                System.out.println("Game state popped! New state: " + stack.peek().getClass().getSimpleName());
            } else {
                System.out.println("Game state popped! No new state.");
            }
        } else {
            current = stack.peek();
        }
    }

    public enum GameState {
        MainMenu, Settings, WorldSelector, InGame, PauseMenu, NewWorld
    }
}
