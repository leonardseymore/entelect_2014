package za.co.entelect.challenge;

import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.agents.*;
import za.co.entelect.challenge.domain.Game;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.groovy.GameFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayOffs {

    private static final Logger logger = LoggerFactory.getLogger(PlayOffs.class);

    private static final List<PacmanAgent> AGENTS = new ArrayList<>();
    static {
        AGENTS.add(new Greedie());
        AGENTS.add(new Greedy());
        AGENTS.add(new Layzie());
        AGENTS.add(new Layzy());
        //AGENTS.add(new Dummie());
        //AGENTS.add(new Eddie());
        AGENTS.add(new Kungus());
        AGENTS.add(new Vladimir());
        AGENTS.add(new Angus());
        AGENTS.add(new Hayw1r3d());
        AGENTS.add(new Attack());
        //AGENTS.add(new Naratu());
        AGENTS.add(new Eek());
        //AGENTS.add(new Mandy());
        //AGENTS.add(new Epa());
    }

    private GameState initialGameState;

    public PlayOffs(GameState initialGameState) {
        this.initialGameState = initialGameState;
    }

    private void letTheGamesBegin() {
        Map<String, Integer> winners = new HashMap<>();
        int[][] rounds = new int[AGENTS.size()][AGENTS.size()];
        for (int i = 0; i < AGENTS.size(); i++) {
            PacmanAgent playerA = AGENTS.get(i);
            for (int j = 0; j < AGENTS.size(); j++) {
                PacmanAgent playerB = AGENTS.get(j);

                Game game = new Game(initialGameState, playerA, playerB);
                GameState gameState = game.getGameState();
                while (!gameState.isGameOver() && gameState.getMovesNoPills() < Constants.MAX_MOVES_NO_PILLS) {
                    game.update();
                }

                if (gameState.getMovesNoPills() >= Constants.MAX_MOVES_NO_PILLS) {
                    logger.info("A={},B={}:MAX_MOVES_NO_PILLS", game.getPlayerAAgentClass(), game.getPlayerBAgentClass());
                    rounds[i][j] = -1;
                } else {
                    String winnerAgent = game.getWinnerAgent();
                    logger.info("A={}({}),B={}({}),WINNER={}", game.getPlayerAAgentClass(), gameState.getPlayerAScore(), game.getPlayerBAgentClass(), gameState.getPlayerBScore(), Util.getWinner(gameState), winnerAgent);
                    if (winners.containsKey(winnerAgent)) {
                        winners.put(winnerAgent, winners.get(winnerAgent) + 1);
                    } else {
                        winners.put(winnerAgent, 1);
                    }
                    rounds[i][j] = Util.getWinner(gameState) == Constants.PLAYER_A ? 1 : 0;
                }
            }
        }

        logger.info("The results are out");
        logger.info("WINNERS:\n{}\n", winners);

        StringBuilder perf = new StringBuilder();
        for (Map.Entry<String, Timer> timer : Util.metrics.getTimers().entrySet()) {
            perf.append(timer.getKey() + ": ~" + String.format("%,.2f", timer.getValue().getSnapshot().getMean() / 1000_000) + "ms\n");
        }
        logger.info("Performance results:\n{}\n", perf);

        StringBuilder resultMatrix = new StringBuilder();
        resultMatrix.append("AvsB");
        for (int i = 0; i < AGENTS.size(); i++) {
            resultMatrix.append(",");
            resultMatrix.append(AGENTS.get(i).getClass().getSimpleName());
        }
        resultMatrix.append("\n");
        for (int i = 0; i < AGENTS.size(); i++) {
            resultMatrix.append(AGENTS.get(i).getClass().getSimpleName());
            for (int j = 0; j < AGENTS.size(); j++) {
                resultMatrix.append(",");
                resultMatrix.append(rounds[i][j]);
            }
            resultMatrix.append("\n");
        }
        logger.info("Rounds:\n{}\n", resultMatrix);

    }

    public static void main(String[] args) throws Exception {
        GameState initialGameState = GameFactory.fromClasspathFile("/initial.state");
        PlayOffs playOffs = new PlayOffs(initialGameState);
        playOffs.letTheGamesBegin();
    }
}
