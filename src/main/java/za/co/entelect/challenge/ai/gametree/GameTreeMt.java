package za.co.entelect.challenge.ai.gametree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Collection;

public class GameTreeMt extends GameTree {

    private static final Logger logger = LoggerFactory.getLogger(GameTreeMt.class);

    // mt with iterative deepening
    public static XY mtdf(GameState gameState, int maxDepth, long timeout) {
        long startTime = System.currentTimeMillis();
        int guess = 0;

        TranspositionTable transpositionTable = new TranspositionTable();
        GameTreeResult result = null;
        int depth = 2;
        for (depth = 2; depth < maxDepth; depth++) {
            result = mtd(transpositionTable, gameState, depth, guess, 1);

            if (System.currentTimeMillis() - startTime > timeout) {
                break;
            }
        }
        logger.debug("MTDF: Player {} MT searched {} levels in {}ms with a best score of {} and a move of {} and TT has {} entries", gameState.getCurrentPlayer(), depth, System.currentTimeMillis() - startTime, result.score, result.move, transpositionTable.size());

        return result.move;
    }

    public static GameTreeResult mtd(TranspositionTable transpositionTable, GameState gameState, int maxDepth, int guess, int iterations) {
        int gamma = 0;
        GameTreeResult result = null;
        for (int i = 0; i < iterations; i++) {
            gamma = guess;
            result = mt(transpositionTable, gameState, maxDepth, 0, gamma - 1);
            guess = result.score;

            if (gamma == guess) {
                break;
            }
        }

        return result;
    }

    // memory enhanced test algorithm
    public static GameTreeResult mt(TranspositionTable transpositionTable, GameState gameState, int maxDepth, int currentDepth, int gamma) {
        long hash = gameState.getHash();
        TranspositionEntry entry = transpositionTable.get(hash);
        if (entry != null && entry.depth > maxDepth - currentDepth) {
            if (entry.minScore > gamma) {
                return new GameTreeResult(entry.minScore, entry.bestMove);
            }
            if (entry.maxScore < gamma) {
                return new GameTreeResult(entry.maxScore, entry.bestMove);
            }
        } else {
            entry = new TranspositionEntry();
            entry.hash = hash;
            entry.depth = maxDepth - currentDepth;
            entry.minScore = Integer.MIN_VALUE;
            entry.maxScore = Integer.MAX_VALUE;
        }

        if (gameState.isGameOver() || currentDepth == maxDepth) {
            entry.minScore = entry.maxScore = evaluate(gameState);
            transpositionTable.put(hash, entry);
            return new GameTreeResult(entry.minScore, null);
        }

        XY bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        XY pos = gameState.getCurrentPosition();
        Collection<SearchNode> neighbors = Search.getAvailableNeighbors(gameState, new SearchNode(pos));
        for (SearchNode neighbor : neighbors) {
            XY move = neighbor.pos;
            GameState gameStateAfterMove = gameState.clone();
            gameStateAfterMove.makeMove(pos, move);

            GameTreeResult recurse = mt(transpositionTable, gameStateAfterMove, maxDepth, currentDepth + 1, -gamma);
            int currentScore = -recurse.score;

            if (currentScore != 0 && currentScore > bestScore) {
                entry.bestMove = move;

                bestScore = currentScore;
                bestMove = move;
            }
        }

        if (bestScore < gamma) {
            entry.maxScore = bestScore;
        } else {
            entry.minScore = bestScore;
        }

        transpositionTable.put(hash, entry);

        return new GameTreeResult(bestScore, bestMove);
    }

    private static int evaluate(GameState gameState) {
        return gameState.getCurrentPlayerScore() - gameState.getOpponentScore();
    }
}
