package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.ai.search.SearchCriteriaFactory;

public class MoveToClosestPill extends MoveToClosestNodeMatch {

    public MoveToClosestPill() {
        this(false);
    }

    public MoveToClosestPill(boolean tactical) {
        super(SearchCriteriaFactory.nodeIsAnyPill, tactical);
    }
}
