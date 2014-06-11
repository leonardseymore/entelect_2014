package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.ai.filters.PillCluster;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Map;

public class SearchCriteriaBiggestCluster implements SearchCriteria {
    public boolean matches(GameState gameState, SearchNode node) {
        // TODO: cache pill clusters with game state
        Map.Entry<XY, Integer> maxCluster = null;
        for (Map.Entry<XY, Integer> entry : PillCluster.getClusteredPills(gameState).entrySet()) {
            if (maxCluster == null || entry.getValue() > maxCluster.getValue()) {
                maxCluster = entry;
            }
        }
        XY biggestCluster = maxCluster.getKey();
        return node.pos.equals(biggestCluster);
    }
}
