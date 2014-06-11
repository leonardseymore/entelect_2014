package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.domain.XY;

public class SearchCriteriaFactory {

    public static final SearchCriteriaAnyPill nodeIsAnyPill = new SearchCriteriaAnyPill();
    public static final SearchCriteriaBonusPill nodeIsBonusPill = new SearchCriteriaBonusPill();
    public static final SearchCriteriaYourInfluence yourInfluence = new SearchCriteriaYourInfluence();
    public static final SearchCriteriaYourPotential yourPotential = new SearchCriteriaYourPotential();
    public static final SearchCriteriaOpponent nodeIsOpponent = new SearchCriteriaOpponent();
    public static final SearchCriteriaBiggestCluster nodeIsBiggestCluster = new SearchCriteriaBiggestCluster();

    public static SearchCriteriaAnd and(SearchCriteria... nodeCriterias) {
        return new SearchCriteriaAnd(nodeCriterias);
    }

    public static SearchCriteriaIsAt isAt(XY point) {
        return new SearchCriteriaIsAt(point);
    }
}
