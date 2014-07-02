package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Collection;

public class Influence {

    public static float[][] getInfluence(GameState gameState, XY pos, Falloff falloff) {
        float[][] result = new float[Constants.WIDTH][Constants.HEIGHT];
        Collection<SearchNode> influence = FloodFillInfluence.getInfluence(gameState, pos);
        for (SearchNode node : influence) {
            result[node.pos.x][node.pos.y] += falloff.get(node.runningCost);
        }
        return result;
    }
}
