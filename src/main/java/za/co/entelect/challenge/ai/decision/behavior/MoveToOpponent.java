package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.ai.search.SearchCriteriaFactory;

public class MoveToOpponent extends MoveToClosestNodeMatch {

    public MoveToOpponent() {
        super(SearchCriteriaFactory.nodeIsOpponent);
    }
}
