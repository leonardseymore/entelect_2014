package za.co.entelect.challenge.ai.decision.behavior;


import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.mcts.UCT;
import za.co.entelect.challenge.ai.mcts.UCTGameState;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Arrays;

public class GuessBestMoveUct extends Task {

    private int maxDepth;
    private long timeout;
    private float heuristic;

    public GuessBestMoveUct(int maxDepth, long timeout, float heuristic) {
        this.maxDepth = maxDepth;
        this.timeout = timeout;
        this.heuristic = heuristic;
    }

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();

        UCTGameState uctGameState = UCT.convert(gameState);
        XY uctPos = UCT.getInstance().uct(uctGameState, maxDepth, timeout, true, false, heuristic);
        XY move = new XY(uctPos.x, uctPos.y);
        if (move == null) {
            return false;
        }

        blackboard.target = move;
        return true;
    }

    @Override
    protected String getLabel() {
        return "GuessBestMove x" + maxDepth;
    }
}
