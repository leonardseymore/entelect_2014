package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.domain.GameState;

public class MoveToTarget extends Task {

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();
        if (blackboard.target == null) {
            return false;
        }

        blackboard.nextMove = blackboard.target;
        return true;
    }
}
