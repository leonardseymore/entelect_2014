package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchBfs;
import za.co.entelect.challenge.ai.search.SearchCriteriaFactory;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Collection;
import java.util.Stack;

public class MoveToHighestPotentialPill extends Task {

    public static final int DEFAULT_NUM_MATCHES = 10;
    private boolean tactical;
    private int numMatches;

    public MoveToHighestPotentialPill() {
        this(false, DEFAULT_NUM_MATCHES);
    }

    public MoveToHighestPotentialPill(boolean tactical, int numMatches) {
        this.tactical = tactical;
        this.numMatches = numMatches;
    }

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();

        XY pos = gameState.getCurrentPosition();
        Stack<SearchNode> matches = Search.bfs(gameState, pos, SearchCriteriaFactory.nodeIsAnyPill, numMatches);
        if (matches.size() == 0) {
            return false;
        }

        SearchNode bestMatch = null;
        float bestPotential = 0;
        for (SearchNode node : matches) {
            float potential = gameState.getInfluenceMap().getPotentialYMap()[node.pos.x][node.pos.y];
            if (potential > bestPotential) {
                bestPotential = potential;
                bestMatch = node;
            }
        }
        if (bestMatch == null) {
            return false;
        }

        Stack<SearchNode> path = SearchNode.pathToNode(bestMatch);
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
