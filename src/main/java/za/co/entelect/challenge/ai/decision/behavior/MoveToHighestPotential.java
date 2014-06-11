package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Collection;

public class MoveToHighestPotential extends Task {

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();

        XY pos = gameState.getCurrentPosition();
        Collection<SearchNode> neighbors = Search.getAvailableNeighbors(gameState, new SearchNode(pos));
        if (neighbors == null) {
            return false;
        }

        float[][] potentialMap = gameState.getInfluenceMap().getPotentialYMap();
        float highestPotential = 0;
        SearchNode bestNeighbor = null;
        for (SearchNode neighbor : neighbors) {
            float potential = potentialMap[neighbor.pos.x][neighbor.pos.y];
            if (potential > highestPotential) {
                highestPotential = potential;
                bestNeighbor = neighbor;
            }
        }

        if (bestNeighbor == null) {
            return false;
        }

        blackboard.nextMove = bestNeighbor.pos;
        return true;
    }
}
