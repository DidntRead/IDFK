package proj.idfk;

public class Main {
    static boolean debug = true;

    public static void main(String[] args) {
        Application app = new Application("IDFK", new Config().parseArguments(args).print());
        app.mainLoop();
        app.dispose();
    }
}
