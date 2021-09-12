package proj.idfk.state;

import proj.idfk.Camera;
import proj.idfk.render.MasterRenderer;

@SuppressWarnings({"EmptyMethod", "unused"})
public interface GameState {
    void on_enter();
    void on_exit();
    void update(float delta);
    void render(MasterRenderer renderer, Camera camera);
}
