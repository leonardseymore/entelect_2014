package za.co.entelect.challenge.ai.decision.behavior;


import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.gametree.GameTreeSearch;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

public class GuessBestMoveBfs extends Task {

    private int maxDepth;

    public GuessBestMoveBfs(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();

        XY move = GameTreeSearch.bfs(gameState, gameState.getCurrentPlayer(), maxDepth, Constants.THINK_TIME);
        if (move == null) {
            return false;
        }

        blackboard.target = move;
        return true;
    }

    @Override
    protected String getLabel() {
        return "GuessBestMoveMinimax x" + maxDepth;
    }
}
