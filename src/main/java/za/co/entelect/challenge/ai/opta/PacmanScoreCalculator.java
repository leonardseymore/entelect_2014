package za.co.entelect.challenge.ai.opta;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.director.simple.SimpleScoreCalculator;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.search.InfluenceMap;
import za.co.entelect.challenge.domain.Game;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.List;

public class PacmanScoreCalculator implements SimpleScoreCalculator<PacmanSolution> {

    @Override
    public SimpleScore calculateScore(PacmanSolution tour) {
        GameState gameState = tour.getGameState();
        InfluenceMap influenceMap = gameState.getInfluenceMap();

        List<Visit> visitList = tour.getVisitList();
        int score = 0;
        for (Visit visit : visitList) {
            Standstill previousStandstill = visit.getPreviousCell();
            if (previousStandstill != null) {
                score -= visit.getDistanceToPreviousCell();
            }

            /*
            // minimize difference between cell potentials
            if (previousStandstill != null) {
                float[][] pot = influenceMap.getyInfluenceMap();
                XY cell = visit.getCell();
                XY previousCell = previousStandstill.getCell();

                int cellPot = (int)(pot[cell.x][cell.y] * 100);
                int previousCellPot = (int)(pot[previousCell.x][previousCell.y] * 100);

                score -= Math.abs(cellPot - previousCellPot);
            }

            // avoid low potentials
            if (previousStandstill != null) {
                float[][] pot = influenceMap.getPillCluster();
                XY cell = visit.getCell();
                XY previousCell = previousStandstill.getCell();

                int cellPot = (int)(pot[cell.x][cell.y] * 100);
                int previousCellPot = (int)(pot[previousCell.x][previousCell.y] * 100);

                score += cellPot;
                score += previousCellPot;
            }
            */
        }
        return SimpleScore.valueOf(score);
    }
}
