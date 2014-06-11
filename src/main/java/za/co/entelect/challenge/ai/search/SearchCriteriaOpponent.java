package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

public class SearchCriteriaOpponent implements SearchCriteria {
    public boolean matches(GameState gameState, SearchNode node) {
        XY opos = gameState.getOpponentPosition();
        return node.pos.equals(opos);
    }
}
