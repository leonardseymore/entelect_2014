package za.co.entelect.challenge.swing;

import za.co.entelect.challenge.domain.Game;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.groovy.GameFactory;

public class AppSwing {
    public static void main(String[] args) throws Exception {
        Game game = GameFactory.fromScriptFile(args[0]);
        GUI app = new GUI(game);
        app.setVisible(true);
        Thread gameThread = new Thread(app);
        gameThread.start();
        //Console console = new Console(new IO(System.in, System.out, System.err), app.getGame());
    }
}
