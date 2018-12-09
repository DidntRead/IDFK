package proj.idfk.state;

import proj.idfk.Application;
import proj.idfk.Window;
import proj.idfk.render.MasterRenderer;
import proj.idfk.world.SaveManager;
import proj.idfk.world.World;

import java.util.ArrayDeque;
import java.util.Deque;

public class GameStateManager {
    private boolean popState = false;
    private proj.idfk.state.GameState current;
    private Deque<proj.idfk.state.GameState> stack;
    private Window window;

    private MainMenu mainMenu;
    private Settings settings;
    private WorldSelector worldSelector;
    private NewWorld newWorld;
    private PauseMenu pauseMenu;
    private InGame inGame;

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
        MainMenu, Settings, WorldSelector, InGame, PauseMenu, NewWorld;
    }
}
