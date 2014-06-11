package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.ai.search.SearchCriteriaFactory;

public class MoveToClosestPotentialPill extends MoveToClosestNodeMatch {

    public MoveToClosestPotentialPill() {
        super(SearchCriteriaFactory.and(
                SearchCriteriaFactory.nodeIsAnyPill,
                SearchCriteriaFactory.yourPotential
        ));
    }
}
