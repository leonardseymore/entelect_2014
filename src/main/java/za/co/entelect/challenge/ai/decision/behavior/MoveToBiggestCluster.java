package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.ai.search.SearchCriteriaFactory;

public class MoveToBiggestCluster extends MoveToClosestNodeMatch {

    public MoveToBiggestCluster() {
        super(SearchCriteriaFactory.and(
                SearchCriteriaFactory.nodeIsBiggestCluster
        ));
    }
}
