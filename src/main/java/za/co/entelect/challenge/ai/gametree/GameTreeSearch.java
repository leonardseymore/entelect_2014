package za.co.entelect.challenge.ai.gametree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

public class GameTreeSearch {

    private static final Logger logger = LoggerFactory.getLogger(GameTreeSearch.class);

    private static GameTreeMinimax gameTreeMinimax = new GameTreeMinimax();
    public static XY minimax(GameState gameState, char player, int maxDepth) {
        return gameTreeMinimax.minimax(gameState, player, maxDepth);
    }

    private static GameTreeNegamax gameTreeNegamax = new GameTreeNegamax();
    public static XY negamax(GameState gameState, int maxDepth) {
        return gameTreeNegamax.negamax(gameState, maxDepth);
    }

    private static GameTreeMt gameTreeMt = new GameTreeMt();
    public static XY mtdf(GameState gameState, int maxDepth, long timeout) {
        return gameTreeMt.mtdf(gameState, maxDepth, timeout);
    }

}

