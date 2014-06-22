package za.co.entelect.challenge.ai.gametree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.ai.mcts.UCTGameState;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.MST;
import za.co.entelect.challenge.domain.XY;

import java.util.Collection;

public class GameTreeMst extends GameTree {

    private static final Logger logger = LoggerFactory.getLogger(GameTreeMst.class);

    // mt with iterative deepening
    public static XY mstd(UCTGameState gameState, int maxDepth, long timeout) {
        long startTime = System.currentTimeMillis();
        int guess = 0;

        TranspositionTable transpositionTable = new TranspositionTable();
        GameTreeResult result = null;
        int depth;
        for (depth = 2; depth <= maxDepth; depth++) {
            result = mstd(transpositionTable, gameState, depth, guess, 1);

            if (System.currentTimeMillis() - startTime > timeout) {
                break;
            }
        }
        logger.debug("MTDF: Player {} MT searched {} levels in {}ms with a best score of {} and a move of {} and TT has {} entries", (char)gameState.getCurrentPlayer(), depth, System.currentTimeMillis() - startTime, result.score, result.move, transpositionTable.size());

        return result.move;
    }

    public static GameTreeResult mstd(TranspositionTable transpositionTable, UCTGameState gameState, int maxDepth, int guess, int iterations) {
        int gamma = 0;
        GameTreeResult result = null;
        for (int i = 0; i < iterations; i++) {
            gamma = guess;
            result = mst(transpositionTable, gameState, maxDepth, 0, gamma - 1);
            guess = result.score;

            if (gamma == guess) {
                break;
            }
        }

        return result;
    }

    // memory enhanced test algorithm
    public static GameTreeResult mst(TranspositionTable transpositionTable, UCTGameState gameState, int maxDepth, int currentDepth, int gamma) {
        long hash = gameState.getHash();
        TranspositionEntry entry = transpositionTable.get(hash);
        if (entry != null && entry.depth > maxDepth - currentDepth) {
            if (entry.minScore > gamma) {
                return new GameTreeResult(gamma, entry.bestMove);
            }
        } else {
            entry = new TranspositionEntry();
            entry.hash = hash;
            entry.depth = maxDepth - currentDepth;
            entry.minScore = Integer.MAX_VALUE;
        }

        if (gameState.isGameOver() || currentDepth == maxDepth) {
            entry.minScore = entry.maxScore = evaluate(gameState);
            transpositionTable.put(hash, entry);
            return new GameTreeResult(entry.minScore, null);
        }

        XY bestMove = null;
        int bestScore = Integer.MAX_VALUE;

        for (XY move : gameState.getMoves()) {
            UCTGameState gameStateAfterMove = gameState.clone();
            gameStateAfterMove.doMove(move, false, currentDepth);

            GameTreeResult recurse = mst(transpositionTable, gameStateAfterMove, maxDepth, currentDepth + 1, gamma);
            //logger.debug("{}|{}{}{}", String.format("%" + (currentDepth + 1) + "s", ""), currentDepth, move, recurse.score);
            int currentScore = recurse.score;

            if (currentScore != 0 && currentScore < bestScore) {
                entry.bestMove = move;

                bestScore = currentScore;
                bestMove = move;
            }
        }

        if (bestScore > gamma) {
            entry.minScore = bestScore;
        }

        transpositionTable.put(hash, entry);

        return new GameTreeResult(bestScore, bestMove);
    }

    private static int evaluate(UCTGameState gameState) {
        MST mst = MST.fromGameState(gameState, gameState.getCurrentPosition());
        return mst.getWeight();
    }
}
