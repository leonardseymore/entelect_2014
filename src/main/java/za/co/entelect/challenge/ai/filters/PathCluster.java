package za.co.entelect.challenge.ai.filters;

import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.search.FloodFillInfluence;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchCriteriaFactory;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.*;

public class PathCluster {

    public static float[][] getCellPotential(GameState gameState) {
        XY pos = gameState.getCurrentPosition();

        float[][] potentials = new float[Constants.WIDTH][Constants.HEIGHT];
        for (XY pill : gameState.getPills()) {
            populate(gameState, potentials, pos, pill, 1);
        }
        for (XY bonusPill : gameState.getBonusPills()) {
            populate(gameState, potentials, pos, bonusPill, 10);
        }

        float max = 0;
        for (int x = 0; x < Constants.WIDTH; x++) {
            for (int y = 0; y < Constants.HEIGHT; y++) {
                if (potentials[x][y] > max) {
                    max = potentials[x][y];
                }
            }
        }

        if (max != 0) {
            for (int x = 0; x < Constants.WIDTH; x++) {
                for (int y = 0; y < Constants.HEIGHT; y++) {
                    potentials[x][y] /= max;
                }
            }
        }

        return potentials;
    }

    private static void populate(GameState gameState, float[][]potentials, XY pos, XY pill, float weight) {
        Stack<SearchNode> path = Search.bfs(gameState, pos, SearchCriteriaFactory.isAt(pill));
        for (SearchNode n : path) {
            potentials[n.pos.x][n.pos.y] += weight;//Util.falloff((int)n.runningCost);
        }
    }
}
