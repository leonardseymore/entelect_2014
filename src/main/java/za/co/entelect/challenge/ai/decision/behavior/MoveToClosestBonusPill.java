package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.ai.search.SearchCriteriaFactory;

public class MoveToClosestBonusPill extends MoveToClosestNodeMatch {

    public MoveToClosestBonusPill() {
        this(false);
    }

    public MoveToClosestBonusPill(boolean tactical) {
        super(SearchCriteriaFactory.nodeIsBonusPill, tactical);
    }
}
