package proj.idfk.state;

import proj.idfk.Application;
import proj.idfk.Window;
import proj.idfk.render.MasterRenderer;

import java.util.ArrayDeque;
import java.util.Deque;

public class GameStateManager {
    private boolean popState = false;
    private proj.idfk.state.GameState current;
    private Deque<proj.idfk.state.GameState> stack;
    private Window window;

    private MainMenu mainMenu;
    private Settings settings;

    public GameStateManager(Application app) {
        this.stack = new ArrayDeque<>();
        this.window = app.getWindow();

        this.mainMenu = new MainMenu(app);
        this.settings = new Settings(app);

        push(GameState.MainMenu);
        signal();
    }

    private proj.idfk.state.GameState instanceOf(GameState gameState) {
        switch (gameState) {
            case InGame:
                return null;
            case MainMenu:
                return mainMenu;
            case PauseMenu:
                return null;
            case Settings:
                return settings;
            case WorldSelector:
                return null;
        }
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

    public void push(GameState newState) {
        proj.idfk.state.GameState state = instanceOf(newState);
        stack.push(state);
        state.on_enter();
        window.registerCallbacks(state);
        System.out.println("New game state: " + state.getClass().getSimpleName());
    }

    public void pop() {
        this.popState = true;
    }

    public void signal() {
        if (popState) {
            proj.idfk.state.GameState last = stack.pop();
            popState = false;
            proj.idfk.state.GameState newState = stack.peek();

            if (newState != null) {
                newState.on_enter();
                window.registerCallbacks(newState);
                current = newState;
                System.out.println("Game state popped! New state: " + stack.peek().getClass().getSimpleName());
            } else {
                System.out.println("Game state popped! No new state.");
            }

            if (last != null) {
                last.on_exit();
            }
        } else {
            current = stack.peek();
        }
    }

    public enum GameState {
        MainMenu, Settings, WorldSelector, InGame, PauseMenu
    }
}
