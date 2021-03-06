package za.co.entelect.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.agents.*;
import za.co.entelect.challenge.domain.Game;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.groovy.GameFactory;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
public class MainClopHayw1r3d {

    private static final Logger logger = LoggerFactory.getLogger(MainClopHayw1r3d.class);

    private static final List<PacmanAgent> AGENTS = new ArrayList<>();
    static {
        //AGENTS.add(new Greedie());
        //AGENTS.add(new Greedy());
        //AGENTS.add(new Layzie());
        //AGENTS.add(new Layzy());
        //AGENTS.add(new Dummie());
        //AGENTS.add(new Eddie());
        //AGENTS.add(new Kungus());
        //AGENTS.add(new Vladimir());
        //AGENTS.add(new Angus());
        //AGENTS.add(new Hayw1r3d());
        AGENTS.add(new Attack());
        //AGENTS.add(new Naratu());
        //AGENTS.add(new Eek());
        //AGENTS.add(new Mandy());
        //AGENTS.add(new Epa());
    }

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Random random = new Random(Long.parseLong(args[1]));
        int numMatches = 0;
        for (int i = 0; i < args.length; i++) {
            if (i > 1) {
                if (i % 2 != 0) {
                    String key = args[i - 1];
                    String value = args[i];
                    if (key.equalsIgnoreCase("numMatches")) {
                        numMatches = Integer.parseInt(value);
                    }
                }
            }
        }

        PacmanAgent playerA = new Hayw1r3d(numMatches);
        PacmanAgent playerB = AGENTS.get(random.nextInt(AGENTS.size()));

        GameState initialGameState = GameFactory.fromClasspathFile("/initial.state");
        challengeAvsB(initialGameState, playerA, playerB);
    }

    private static void challengeAvsB(GameState initialGameState, PacmanAgent playerA, PacmanAgent playerB) {
        char round1 = Util.getWinner(playRound(initialGameState, playerA, playerB));
        char round2 = Util.getWinner(playRound(initialGameState, playerB, playerA));
        if (round2 == Constants.PLAYER_A) {
            round2 = Constants.PLAYER_B;
        } else if (round2 == Constants.PLAYER_B) {
            round2 = Constants.PLAYER_A;
        }

        if (round1 == round2) {
            if (round1 == Constants.PLAYER_A) {
                System.out.println("W");
            } else if (round1 == Constants.PLAYER_B) {
                System.out.println("L");
            } else {
                System.out.println("Round 1 " + round1 + " round 2 " + round2 + " " + playerA.getClass().getSimpleName() + " vs. " + playerB.getClass().getSimpleName());
            }
        } else {
            if ((round1 == Constants.PLAYER_A || round1 == Constants.PLAYER_B)
                    && (round2 == Constants.PLAYER_A || round2 == Constants.PLAYER_B)) {
                System.out.println("D");
            } else {
                System.out.println("Round 1 " + round1 + " round 2 " + round2 + " " + playerA.getClass().getSimpleName() + " vs. " + playerB.getClass().getSimpleName() );
            }
        }
    }

    private static GameState playRound(GameState initialGameState, PacmanAgent playerA, PacmanAgent playerB) {
        Game game = new Game(initialGameState, playerA, playerB);
        GameState gameState = game.getGameState();
        while (!gameState.isGameOver() && gameState.getMovesNoPills() < Constants.MAX_MOVES_NO_PILLS) {
            game.update();
        }
        return gameState;
    }
}
