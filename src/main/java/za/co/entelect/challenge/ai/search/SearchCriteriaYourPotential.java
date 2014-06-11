package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.domain.GameState;

public class SearchCriteriaYourPotential implements SearchCriteria {

    public boolean matches(GameState gameState, SearchNode node) {
        InfluenceMap influenceMap = gameState.getInfluenceMap();
        return influenceMap.getPotentialYMap()[node.pos.x][node.pos.y] > 0;
    }
}
