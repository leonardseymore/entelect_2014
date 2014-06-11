package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.domain.GameState;

/**
* Created by leonardseymore on 2014/04/23.
*/
public class SearchCriteriaBonusPill implements SearchCriteria {
    public boolean matches(GameState gameState, SearchNode node) {
        char c = gameState.getCell(node.pos);
        return c == Constants.BONUS_PILL;
    }
}
