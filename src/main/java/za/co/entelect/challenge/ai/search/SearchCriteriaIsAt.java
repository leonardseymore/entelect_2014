package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

/**
* Created by leonardseymore on 2014/04/23.
*/
public class SearchCriteriaIsAt implements SearchCriteria {

    public XY point;

    public SearchCriteriaIsAt(XY point) {
        this.point = point;
    }

    public boolean matches(GameState gameState, SearchNode node) {
        return node.pos.equals(point);
    }
}
