package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.ai.search.SearchCriteriaFactory;

public class MoveToBiggestClusterYourInfluence extends MoveToClosestNodeMatch {

    public MoveToBiggestClusterYourInfluence() {
        super(SearchCriteriaFactory.and(
                SearchCriteriaFactory.nodeIsBiggestCluster,
                SearchCriteriaFactory.yourInfluence
        ));
    }
}
