package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchCriteria;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Stack;

public class MoveToClosestNodeMatch extends Task {

    private SearchCriteria searchCriteria;
    private boolean tactical;

    public MoveToClosestNodeMatch(SearchCriteria searchCriteria) {
        this(searchCriteria, false);
    }

    public MoveToClosestNodeMatch(SearchCriteria searchCriteria, boolean tactical) {
        this.searchCriteria = searchCriteria;
        this.tactical = tactical;
    }

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();

        XY pos = gameState.getCurrentPosition();
        Stack<SearchNode> path = Search.bfs(gameState, pos, searchCriteria);
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
