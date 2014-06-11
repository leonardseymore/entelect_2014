package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.ai.search.SearchCriteriaFactory;

public class MoveToClosestInfluenceBonusPill extends MoveToClosestNodeMatch {

    public MoveToClosestInfluenceBonusPill() {
        super(SearchCriteriaFactory.and(
                SearchCriteriaFactory.nodeIsBonusPill,
                SearchCriteriaFactory.yourInfluence
        ));
    }
}
