package za.co.entelect.challenge.ai.gametree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Collection;

public class GameTreeMinimax extends GameTree {

    private static final Logger logger = LoggerFactory.getLogger(GameTreeMinimax.class);

    public XY minimax(GameState gameState, char player, int maxDepth) {
        long startTime = System.currentTimeMillis();

        GameTreeResult result = minimax(gameState, player, player, maxDepth, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

        logger.debug("MINIMAX: Player {} MT searched {} levels in {}ms with a best score of {} and a move of {}", player, maxDepth, System.currentTimeMillis() - startTime, result.score, result.move);
        return result.move;
    }

    public GameTreeResult minimax(GameState gameState, char player, char maxPlayer, int maxDepth, int currentDepth, int alpha, int beta) {
        if (gameState.isGameOver() || currentDepth == maxDepth) {
            return new GameTreeResult(evaluate(gameState, player), null);
        }

        XY pos = gameState.getCurrentPosition();
        Collection<SearchNode> neighbors = Search.getAvailableNeighbors(gameState, new SearchNode(pos));
        if (Constants.CAN_STAND_STILL) {
            neighbors.add(new SearchNode(pos));
        }
        if (maxPlayer == player) {
            XY move = null;
            for (SearchNode neighbor : neighbors) {
                GameState gameStateAfterMove = gameState.clone();
                gameStateAfterMove.makeMove(pos, neighbor.pos);

                GameTreeResult recurse = minimax(gameStateAfterMove, Util.getOtherPlayer(player), maxPlayer, maxDepth, currentDepth + 1, alpha, beta);
                if (recurse.score > alpha) {
                    alpha = recurse.score;
                    move = neighbor.pos;
                }
                if (alpha >= beta) {
                    break;
                }
            }
            return new GameTreeResult(alpha, move);
        } else {
            XY move = null;
            for (SearchNode neighbor : neighbors) {
                GameState gameStateAfterMove = gameState.clone();
                gameStateAfterMove.makeMove(pos, neighbor.pos);

                GameTreeResult recurse = minimax(gameStateAfterMove, Util.getOtherPlayer(player), maxPlayer, maxDepth, currentDepth + 1, alpha, beta);
                if (beta > recurse.score) {
                    beta = recurse.score;
                    move = neighbor.pos;
                }
                if (beta <= alpha) {
                    break;
                }
            }
            return new GameTreeResult(beta, move);
        }
    }

    private int evaluate(GameState gameState, char currentPlayer) {
        return gameState.getPlayerScore(currentPlayer) - gameState.getPlayerScore(Util.getOtherPlayer(currentPlayer));
    }
}
