package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.ai.search.SearchCriteriaFactory;

public class MoveToClosestInfluencePill extends MoveToClosestNodeMatch {

    public MoveToClosestInfluencePill() {
        super(SearchCriteriaFactory.and(
                SearchCriteriaFactory.nodeIsAnyPill,
                SearchCriteriaFactory.yourInfluence
        ));
    }
}
