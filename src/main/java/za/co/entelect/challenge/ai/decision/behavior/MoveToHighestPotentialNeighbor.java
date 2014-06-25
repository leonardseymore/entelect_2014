package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchCriteriaFactory;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Set;
import java.util.Stack;

public class MoveToHighestPotentialNeighbor extends Task {

    private boolean tactical;

    public MoveToHighestPotentialNeighbor() {
        this(false);
    }

    public MoveToHighestPotentialNeighbor(boolean tactical) {
        this.tactical = tactical;
    }

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();

        XY pos = gameState.getCurrentPosition();
        XY bestMatch = null;
        float bestPotential = 0;
        for (SearchNode node : Search.getAvailableNeighbors(gameState, new SearchNode(pos))) {
            float potential = gameState.getInfluenceMap().getPotentialYMap()[node.pos.x][node.pos.y];
            if (potential > bestPotential) {
                bestPotential = potential;
                bestMatch = node.pos;
            }
        }
        if (bestMatch == null) {
            return false;
        }

        blackboard.nextMove = bestMatch;
        return true;
    }
}
