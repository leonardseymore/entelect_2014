package za.co.entelect.challenge.ai.gametree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Collection;

public class GameTreeNegamax extends GameTree {

    private static final Logger logger = LoggerFactory.getLogger(GameTreeNegamax.class);

    public XY negamax(GameState gameState, int maxDepth) {
        long startTime = System.currentTimeMillis();
        GameTreeResult result = negamax(gameState, maxDepth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        logger.debug("NEGAMAX: Player {} MT searched {} levels in {}ms with a best score of {} and a move of {}", gameState.getCurrentPlayer(), maxDepth, System.currentTimeMillis() - startTime, result.score, result.move);
        return result.move;
    }

    private GameTreeResult negamax(GameState gameState, int maxDepth, int currentDepth, int alpha, int beta) {
        if (gameState.isGameOver() || currentDepth == maxDepth) {
            return new GameTreeResult(evaluate(gameState), null);
        }

        XY bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        XY pos = gameState.getCurrentPosition();
        Collection<SearchNode> neighbors = Search.getAvailableNeighbors(gameState, new SearchNode(pos));
        if (Constants.CAN_STAND_STILL) {
            neighbors.add(new SearchNode(pos));
        }
        for (SearchNode neighbor : neighbors) {
            XY move = neighbor.pos;
            GameState gameStateAfterMove = gameState.clone();
            gameStateAfterMove.makeMove(pos, move);

            GameTreeResult recurse = negamax(gameStateAfterMove, maxDepth, currentDepth + 1, -beta, -Math.max(alpha, bestScore));
            int currentScore = -recurse.score;

            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = move;

                if (bestScore >= beta) {
                    return new GameTreeResult(bestScore, bestMove);
                }
            }
        }

        return new GameTreeResult(bestScore, bestMove);
    }

    protected int evaluate(GameState gameState) {
        return gameState.getCurrentPlayerScore() - gameState.getOpponentScore();
    }
}
