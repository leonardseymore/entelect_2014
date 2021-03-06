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

public class MoveToHighestPotential extends Task {

    private boolean tactical;

    public MoveToHighestPotential() {
        this(false);
    }

    public MoveToHighestPotential(boolean tactical) {
        this.tactical = tactical;
    }

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();

        XY pos = gameState.getCurrentPosition();
        Set<XY> pills = Util.getAllPills(gameState);
        XY bestMatch = null;
        float bestPotential = 0;
        for (XY node : pills) {
            float potential = gameState.getInfluenceMap().getPotentialYMap()[node.x][node.y];
            if (potential > bestPotential) {
                bestPotential = potential;
                bestMatch = node;
            }
        }
        if (bestMatch == null) {
            return false;
        }


        Stack<SearchNode> path = Search.bfs(gameState, bestMatch, SearchCriteriaFactory.nodeIsAnyPill);
        if (path == null || path.empty()) {
            return false;
        }

        if (tactical) {
            SearchNode dest = path.firstElement();
            path = Search.tactical(gameState, pos, dest.pos);
            if (path == null) {
                return false;
            }
        }

        SearchNode target = path.peek();
        while ((pos.equals(target.pos)) && !path.isEmpty()) {
            target = path.pop();
        }
        blackboard.nextMove = target.pos;
        blackboard.searchPath = path;
        return true;
    }
}
