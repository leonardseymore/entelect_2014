package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchCriteriaFactory;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Stack;

public class IsAnyPillCloserThan extends Task {

    private int dist;

    public IsAnyPillCloserThan(int dist) {
        this.dist = dist;
    }

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        XY pos = gameState.getCurrentPosition();
        Stack<SearchNode> path = Search.bfs(gameState, pos, SearchCriteriaFactory.nodeIsAnyPill);
        if (path == null) {
            return false;
        }
        return path.size() < dist;
    }

    @Override
    protected String getLabel() {
        return "IsAnyPillCloserThan " + dist + "?";
    }
}
