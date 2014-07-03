package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.ai.filters.PillCluster;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Map;

public class SearchCriteriaBiggestCluster implements SearchCriteria {
    public boolean matches(GameState gameState, SearchNode node) {
        return gameState.getInfluenceMap().inBiggestPillCluster(node.pos);
    }
}
