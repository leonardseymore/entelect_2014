package za.co.entelect.challenge.ai.opta;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.XmlSolverFactory;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.*;

public class Opta {

    public static List<XY> moves(GameState gameState) {
        return moves(solve(gameState), gameState);
    }

    public static List<XY> moves(List<Visit> visits, GameState gameState) {
        XY pos = gameState.getCurrentPosition();

        Set<XY> open = Util.clone(gameState.getPills());
        List<XY> moves = new ArrayList<>();
        XY currentPos = pos;
        while (!open.isEmpty()) {
            for (Visit visit : visits) {
                if (visit.getPreviousStandstill().getCell().equals(currentPos)) {
                    moves.add(visit.getCell());
                    currentPos = visit.getCell();
                    open.remove(currentPos);
                    continue;
                }
            }
        }

        return moves;
    }

    public static List<Visit> solve(GameState gameState) {
        SolverFactory solverFactory = new XmlSolverFactory("/opta/pacmanSolverConfig.xml");
        Solver solver = solverFactory.buildSolver();

        PacmanSolution unsolved = new PacmanSolution();
        unsolved.setGameState(gameState);
        List<XY> pills = new ArrayList<>(Util.getAllPills(gameState));
        unsolved.setPills(pills);

        List<Visit> visits = new ArrayList<>();
        for (XY pill : pills) {
            visits.add(new Visit(pill));
        }
        unsolved.setVisitList(visits);

        unsolved.setDomicileList(Collections.singletonList(new Domicile(gameState.getCurrentPosition())));

        solver.setPlanningProblem(unsolved);
        solver.solve();

        PacmanSolution solved = (PacmanSolution) solver.getBestSolution();
        return solved.getVisitList();
    }
}
