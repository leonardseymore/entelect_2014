package za.co.entelect.challenge.ai.opta;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.XmlSolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;
import za.co.entelect.challenge.groovy.GameFactory;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pack items into buckets
 * - Empty buckets are good
 */
public class PacmanApp {

    public static final Logger logger = LoggerFactory.getLogger(PacmanApp.class);

    public static void main(String[] args) throws FileNotFoundException {
        GameState gameState = GameFactory.fromClasspathFile("/initial.state");
        List<Visit> visits = Opta.solve(gameState);

        StringBuilder displayString = new StringBuilder();
        for (Visit visit : visits) {
            displayString.append(visit.getPreviousStandstill().getCell() + " -> " + visit.getCell());
            displayString.append("\n");
        }

        logger.info("Best Solution:\n{}", displayString);
    }
}
