package za.co.entelect.challenge.ai.decision.behavior;


import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.gametree.GameTreeSearch;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

public class GuessBestMoveMinimax extends Task {

    private int maxDepth;

    public GuessBestMoveMinimax(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();
        XY move = GameTreeSearch.minimax(gameState, gameState.getCurrentPlayer(), maxDepth);
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
