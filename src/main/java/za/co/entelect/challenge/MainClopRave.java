package za.co.entelect.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.agents.Epa;
import za.co.entelect.challenge.agents.Greedie;
import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.domain.Game;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;
import za.co.entelect.challenge.groovy.GameFactory;

import java.io.FileNotFoundException;

/**
 * Tune parameter using http://remi.coulom.free.fr/CLOP/
 * Arguments are:
 * #1: processor id (symbolic name, typically a machine name to ssh to)
 * #2: seed (integer)
 * #3: parameter id of first parameter (symbolic name)
 * #4: value of first parameter (float)
 * #5: parameter id of second parameter (optional)
 * #6: value of second parameter (optional)
 * ...
 * <p/>
 * This program should write the game outcome to its output:
 * W = win
 * L = loss
 * D = draw
 * <p/>
 * For instance:
 * $ ./Dummy node-01 4 param 0.2
 * W
 */
public class MainClopRave {

    private static final Logger logger = LoggerFactory.getLogger(MainClopRave.class);

    public static void main(String[] args) throws FileNotFoundException {
        for (int i = 0; i < args.length; i++) {
           // logger.info("{} = {}", i, args[i]);
        }

        PacmanAgent playerA = new Epa(Constants.THINK_TIME, Float.parseFloat(args[3]));
        PacmanAgent playerB = new Greedie();

        GameState initialGameState = GameFactory.fromClasspathFile("/initial.state");
        Game game = new Game(initialGameState, playerA, playerB);
        GameState gameState = game.getGameState();
        while (!gameState.isGameOver() && gameState.getMovesNoPills() < Constants.MAX_MOVES_NO_PILLS) {
            game.update();
        }

        switch (Util.getWinner(gameState)) {
            case Constants.PLAYER_A:
                System.out.println("W");
                break;
            case Constants.PLAYER_B:
                System.out.println("L");
                break;
            case '=':
                System.out.println("D");
                break;
            default:
                System.out.println("X");
        }
    }
}
