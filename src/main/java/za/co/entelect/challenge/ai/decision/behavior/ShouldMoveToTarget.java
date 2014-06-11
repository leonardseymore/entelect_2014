package za.co.entelect.challenge.ai.decision.behavior;


import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.search.InfluenceMap;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

public class ShouldMoveToTarget extends Task {
    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();
        XY y = gameState.getCurrentPosition();
        XY target = blackboard.target;
        InfluenceMap influenceMap = gameState.getInfluenceMap();
        float currentYPotential = influenceMap.getTotalYPotential();
        float currentOPotential = influenceMap.getTotalOPotential();

        GameState gameStateAfterMove = gameState.clone();
        gameStateAfterMove.makeMove(y, target, GameState.UPDATE_INFLUENCE_MAP);

        InfluenceMap influenceMapAfterMove = gameStateAfterMove.getInfluenceMap();
        float futureYPotential = influenceMapAfterMove.getTotalOPotential();
        float futureOPotential = influenceMapAfterMove.getTotalYPotential();

        if (futureYPotential > currentYPotential && futureOPotential < currentOPotential) {
            return true;
        }

        return false;
    }

    @Override
    protected String getLabel() {
        return "ShouldMoveInFrontOfOpponent?";
    }
}
