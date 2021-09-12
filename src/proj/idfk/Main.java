package proj.idfk;

import org.lwjgl.system.Configuration;

public class Main {
    public static boolean debug = true;

    public static void main(String[] args) {
        Configuration.STACK_SIZE.set(32768);
        Application app = new Application("IDFK", new Config().parseArguments(args).print());
        app.mainLoop();
        app.dispose();
    }
}
