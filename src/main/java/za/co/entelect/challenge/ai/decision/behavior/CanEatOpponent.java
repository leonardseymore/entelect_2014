package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

public class CanEatOpponent extends Task {
    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();
        XY y = gameState.getCurrentPosition();
        XY o = gameState.getOpponentPosition();

        if (Util.inSpawnZone(y)) {
            return false;
        }

        if (Util.isBesides(y, o) && Util.canMoveTo(gameState, o, y)) {
            blackboard.target = o;
            return true;
        }

        return false;
    }

    @Override
    protected String getLabel() {
        return "CanEatOpponent?";
    }
}
