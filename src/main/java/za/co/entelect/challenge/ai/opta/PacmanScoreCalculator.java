package za.co.entelect.challenge.ai.opta;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.director.simple.SimpleScoreCalculator;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.search.InfluenceMap;
import za.co.entelect.challenge.domain.GameState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PacmanScoreCalculator implements SimpleScoreCalculator<PacmanSolution> {

    @Override
    public SimpleScore calculateScore(PacmanSolution tour) {
        List<Visit> visitList = tour.getVisitList();
        Set<Visit> tailVisitSet = new HashSet<>(visitList);
        int score = 0;
        for (Visit visit : visitList) {
            Standstill previousStandstill = visit.getPreviousStandstill();
            if (previousStandstill != null) {
                score -= visit.getDistanceToPreviousStandstill();
                if (previousStandstill instanceof Visit) {
                    tailVisitSet.remove(previousStandstill);
                }
            }
        }
        if (tour.getDomicileList().size() != 1) {
            throw new UnsupportedOperationException(
                    "The domicileList (" + tour.getDomicileList() + ") should be a singleton.");
        }
        Domicile domicile = tour.getDomicileList().get(0);
        for (Visit tailVisit : tailVisitSet) {
            if (tailVisit.getPreviousStandstill() != null) {
                score -= Util.mazeDistance(domicile.getCell(), tailVisit.getCell());
            }
        }
        return SimpleScore.valueOf(score);
    }
}
