package za.co.entelect.challenge.ai.gametree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.mcts.UCTGameState;
import za.co.entelect.challenge.domain.XY;

import java.util.*;

public class GameTreeBfs {

    private static final Logger logger = LoggerFactory.getLogger(GameTreeBfs.class);

    public XY bfs(UCTGameState gameState, char player, int maxDepth) {
        GameTreeResultBfs result = bfs(gameState, player, maxDepth, 0);
        return result.move;
    }

    public GameTreeResultBfs bfs(UCTGameState gameState, char player, int maxDepth, int currentDepth) {
        if (gameState.isGameOver() || currentDepth == maxDepth) {
            return new GameTreeResultBfs(evaluate(gameState, player), null);
        }

        List<XY> moves = gameState.getMoves();
        float gamma = 0;
        XY move = null;
        for (XY neighbor : moves) {
            UCTGameState gameStateAfterMove = gameState.clone();
            gameStateAfterMove.doMove(neighbor, false, currentDepth);

            GameTreeResultBfs recurse = bfs(gameStateAfterMove, player, maxDepth, currentDepth + 1);
            //logger.debug("{}|{}{}{}", String.format("%" + (currentDepth + 1) + "s", ""), currentDepth, neighbor, recurse.score);
            if (recurse.score > gamma) {
                gamma = recurse.score;
                move = neighbor;
            }
        }
        return new GameTreeResultBfs(gamma, move);
    }

    private float evaluate(UCTGameState gameState, char currentPlayer) {
        return gameState.getWeightedScore((byte)currentPlayer);
    }
}

class GameTreeResultBfs {
    XY move;
    float score;

    GameTreeResultBfs(float score, XY move) {
        this.score = score;
        this.move = move;
    }
}

