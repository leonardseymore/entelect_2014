package za.co.entelect.challenge.ai.decision.behavior;


import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.gametree.GameTreeSearch;
import za.co.entelect.challenge.ai.mcts.UCT;
import za.co.entelect.challenge.ai.mcts.UCTGameState;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Stack;

public class GuessBestMoveMst extends Task {

    private int maxDepth;

    public GuessBestMoveMst(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();

        XY pos = gameState.getCurrentPosition();
        Stack<SearchNode> path = Search.mst(gameState, pos);
        if (path == null || path.empty()) {
            return false;
        }

        SearchNode target = path.peek();
        while ((pos.equals(target.pos)) && !path.isEmpty()) {
            target = path.pop();
        }
        blackboard.nextMove = target.pos;
        blackboard.searchPath = path;
        return true;
    }

    @Override
    protected String getLabel() {
        return "GuessBestMoveMst x" + maxDepth;
    }
}
