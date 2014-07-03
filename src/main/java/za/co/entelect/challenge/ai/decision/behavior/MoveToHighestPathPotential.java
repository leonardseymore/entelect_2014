package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.filters.PathCluster;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Arrays;

public class MoveToHighestPathPotential extends Task {


    public MoveToHighestPathPotential() {

    }


    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();

        float[][] potentials = PathCluster.getCellPotential(gameState);
        XY pos = gameState.getCurrentPosition();
        XY bestMatch = null;
        float bestPotential = 0;
        for (SearchNode node : Search.getAvailableNeighbors(gameState, new SearchNode(pos))) {
            float potential = potentials[node.pos.x][node.pos.y];
            if (potential > bestPotential) {
                bestPotential = potential;
                bestMatch = node.pos;
            }
        }

        boolean debug = false;
        if (debug) {
            for (float[] potential : potentials) {
                for (float v : potential) {
                    System.out.print(((int)(v * 255)) + " ");
                }
                System.out.println("");
            }
        }

        if (bestMatch == null) {
            return false;
        }

        blackboard.nextMove = bestMatch;
        return true;
    }
}
