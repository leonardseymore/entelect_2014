package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.domain.GameState;

/**
* Created by leonardseymore on 2014/04/23.
*/
public class SearchCriteriaAnd implements SearchCriteria {

    public SearchCriteria[] nodeCriterias;

    public SearchCriteriaAnd(SearchCriteria... nodeCriterias) {
        this.nodeCriterias = nodeCriterias;
    }

    public boolean matches(GameState gameState, SearchNode node) {
        for (SearchCriteria nodeCriteria : nodeCriterias) {
            if (!nodeCriteria.matches(gameState, node)) {
                return false;
            }
        }
        return true;
    }
}
